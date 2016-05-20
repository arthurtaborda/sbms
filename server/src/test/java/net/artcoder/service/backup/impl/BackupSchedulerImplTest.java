package net.artcoder.service.backup.impl;

import net.artcoder.domain.*;
import net.artcoder.domain.exception.AlreadyActiveException;
import net.artcoder.domain.exception.BackupNotFoundException;
import net.artcoder.persistence.repository.BackupRepository;
import net.artcoder.service.backup.BackupStatusService;
import net.artcoder.service.backup.task.SendTimeoutTask;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Future;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class BackupSchedulerImplTest {

	private Backup backup;
	private IP ip;

	BackupSchedulerImpl backupScheduler;
	private BackupStatusService backupStatusService;

	@Before
	public void setUp() throws Exception {
		BackupQueue queue = new ConcurrentBackupQueue(1);
		TaskScheduler scheduler = new ConcurrentTaskScheduler();

		backup = new Backup("id", "s1", "sd1", "d1", "dd1");
		ip = new IP("255.255.255.255");

		backupStatusService = new BackupStatusService() {
			@Override
			public void backupStatus(String backupId, Date date, BackupExecutionStatus st, String message) throws BackupNotFoundException {
				throw new BackupNotFoundException();
			}
		};

		backupScheduler = new BackupSchedulerImpl(queue, scheduler, mock(BackupRepository.class));
	}

	@Test
	public void testSchedule() throws Exception {
		BackupSchedule futureTask = getFuture();

		assertEquals(1, backupScheduler.backupsScheduledTasks.size());
		assertTrue(futureTask.isActive());
	}

	private BackupSchedule getFuture() {
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, 2);
		Date nextTwoSeconds = now.getTime();
		backupScheduler.schedule(backup, ip, nextTwoSeconds);
		BackupSchedule futureTask = backupScheduler.backupsScheduledTasks.get(backup.getId());
		assertNotNull(futureTask);
		return futureTask;
	}

	@Test
	public void testEnableDisableBackup() throws Exception, AlreadyActiveException {
		BackupSchedule futureTask = getFuture();

		boolean wasDisabledBefore = !futureTask.isActive();
		backupScheduler.disableBackup(backup.getId());
		boolean isDisabledAfter = !futureTask.isActive();
		backupScheduler.enableBackup(backup.getId());
		boolean isEnabledAfter = futureTask.isActive();

		assertFalse(wasDisabledBefore);
		assertTrue(isDisabledAfter);
		assertTrue(isEnabledAfter);
	}

	@Test
	public void testDeleteBackup() throws Exception {
		BackupSchedule futureTask = getFuture();

		backupScheduler.deleteBackup(backup.getId());

		assertFalse(futureTask.isActive());
		assertEquals(0, backupScheduler.backupsScheduledTasks.size());
	}

	@Test
	public void testStartTimeoutForBackup() throws Exception {

		String id = "id";
		backupScheduler.startTimeoutForBackup(1000, id, new SendTimeoutTask(id, backupStatusService));

		Map<String, Future> backupsTimeout = backupScheduler.backupsTimeout;
		Future futureTimeout = backupsTimeout.get(id);

		assertEquals(1, backupsTimeout.size());
		assertNotNull(futureTimeout);
		assertFalse(futureTimeout.isCancelled());
	}
}