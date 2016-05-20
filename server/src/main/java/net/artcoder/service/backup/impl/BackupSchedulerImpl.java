package net.artcoder.service.backup.impl;

import lombok.extern.slf4j.Slf4j;
import net.artcoder.domain.Backup;
import net.artcoder.domain.BackupQueue;
import net.artcoder.domain.BackupSchedule;
import net.artcoder.domain.IP;
import net.artcoder.domain.exception.AlreadyActiveException;
import net.artcoder.domain.exception.AlreadyDisabledException;
import net.artcoder.domain.exception.BackupNotFoundException;
import net.artcoder.persistence.repository.BackupRepository;
import net.artcoder.service.backup.task.AddBackupToQueue;
import net.artcoder.service.backup.BackupScheduler;
import net.artcoder.service.backup.task.SendTimeoutTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class BackupSchedulerImpl implements BackupScheduler {

	private BackupQueue backupQueue;
	private TaskScheduler taskScheduler;
	private BackupRepository backupRepository;

	final Map<String, Future> backupsTimeout = new HashMap<>();
	final Map<String, BackupSchedule> backupsScheduledTasks = new HashMap<>();

	@Autowired
	public BackupSchedulerImpl(BackupQueue backupQueue, TaskScheduler taskScheduler,
							   BackupRepository backupRepository) {
		this.backupQueue = backupQueue;
		this.taskScheduler = taskScheduler;
		this.backupRepository = backupRepository;
	}

	@Override
	public void schedule(Backup backup, IP address, Date date) {
		log.debug("Scheduling backup " + backup.getId());
		AddBackupToQueue task = new AddBackupToQueue(backup, address, backupQueue, backupRepository);

		ScheduledFuture<?> scheduledFuture;
		scheduledFuture = taskScheduler.schedule(task, date);

		BackupSchedule schedule = new BackupSchedule(scheduledFuture, task, date);

		backupsScheduledTasks.put(backup.getId(), schedule);
	}

	@Override
	public void enableBackup(String backupId) throws AlreadyActiveException, BackupNotFoundException {
		BackupSchedule schedule = backupsScheduledTasks.get(backupId);
		if (schedule != null) {
			schedule.setFuture(taskScheduler.schedule(schedule.getTask(), schedule.getDate()));
		} else {
			throw new BackupNotFoundException("No backup scheduled found for id " + backupId);
		}
	}

	@Override
	public void disableBackup(String backupId) throws BackupNotFoundException, AlreadyDisabledException {
		BackupSchedule schedule = backupsScheduledTasks.get(backupId);
		if (schedule != null) {
			schedule.cancel();
		} else {
			throw new BackupNotFoundException("No backup scheduled found for id " + backupId);
		}
	}

	@Override
	public void deleteBackup(String backupId) throws BackupNotFoundException {
		try {
			disableBackup(backupId);
		} catch (AlreadyDisabledException ignored) {
		}
		backupsScheduledTasks.remove(backupId);
		cancelTimeout(backupId);
	}

	@Override
	public boolean hasTimeOut(String backupId) {
		Future timeout = backupsTimeout.get(backupId);
		return timeout != null && !timeout.isCancelled();
	}

	@Override
	public boolean hasSchedule(String backupId) {
		BackupSchedule schedule = backupsScheduledTasks.get(backupId);
		return schedule != null && schedule.isActive();
	}

	@Override
	public void cancelTimeout(String backupId) {
		Future timeoutFuture = backupsTimeout.get(backupId);
		if(timeoutFuture != null) {
			timeoutFuture.cancel(false);
			backupsTimeout.remove(backupId);
		}
	}

	@Override
	public void startTimeoutForBackup(Integer timeout, String backupId, SendTimeoutTask sendTimeoutTask) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MILLISECOND, timeout);
		Future timeoutFuture = taskScheduler.schedule(sendTimeoutTask, calendar.getTime());
		backupsTimeout.put(backupId, timeoutFuture);
	}
}
