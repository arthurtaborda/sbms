package net.artcoder.service.backup.impl;

import lombok.extern.slf4j.Slf4j;
import net.artcoder.domain.*;
import net.artcoder.domain.exception.BackupNotFoundException;
import net.artcoder.domain.exception.InvalidIPAddressException;
import net.artcoder.persistence.entity.BackupEntity;
import net.artcoder.persistence.entity.BackupExecutionEntity;
import net.artcoder.persistence.repository.BackupExecutionRepository;
import net.artcoder.persistence.repository.BackupRepository;
import net.artcoder.service.backup.BackupScheduler;
import net.artcoder.service.backup.BackupStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class BackupStatusServiceImpl implements BackupStatusService {

	private BackupQueue backupQueue;
	private BackupRepository backupRepository;
	private BackupScheduler backupScheduler;
	private BackupExecutionRepository backupExecutionRepository;

	@Autowired
	public BackupStatusServiceImpl(BackupRepository backupRepository, BackupQueue backupQueue,
								   BackupScheduler backupScheduler, BackupExecutionRepository backupExecutionRepository) {
		this.backupQueue = backupQueue;
		this.backupRepository = backupRepository;
		this.backupScheduler = backupScheduler;
		this.backupExecutionRepository = backupExecutionRepository;
	}

	@Override
	@Transactional
	public void backupStatus(String backupId, Date date, BackupExecutionStatus st, String message) throws
			BackupNotFoundException {
		BackupEntity backupEntity = backupRepository.findOne(backupId);

		if(backupEntity == null) {
			throw new BackupNotFoundException();
		}

		String id = UUID.randomUUID().toString();
		BackupExecutionEntity executionEntity = new BackupExecutionEntity(id, st, backupEntity, date);
		executionEntity.setMessage(message);

		backupExecutionRepository.save(executionEntity);

		if(st == BackupExecutionStatus.DONE
				|| st == BackupExecutionStatus.TIMEOUT
				|| st == BackupExecutionStatus.ERROR) {

			backupQueue.freeSlot(backupId);
			backupScheduler.cancelTimeout(backupId);
			try {
				backupScheduler.deleteBackup(backupId); //in case this backup was scheduled
			} catch (Exception ignored){}
		}

		if (st == BackupExecutionStatus.DONE) {
			backupRepository.done(backupId);
		} else if (st == BackupExecutionStatus.ERROR) {
			boolean reschedule = false;
			try {
				reschedule = reschedule(backupEntity);

			} catch (InvalidIPAddressException e) {
				e.printStackTrace();
				log.error("Invalid IP Address in the database");
			}

			if (!reschedule) {
				backupEntity.setState(BackupState.ERROR);
				backupRepository.save(backupEntity);
			}
		}
	}

	private boolean reschedule(BackupEntity backupEntity) throws InvalidIPAddressException {
		Long rescheduleTimeout = backupEntity.getRescheduleTimeout();
		if(rescheduleTimeout == null || rescheduleTimeout <= 0 ||
				backupEntity.getCurrentTries() >= backupEntity.getMaximumReschedules()) {
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MILLISECOND, rescheduleTimeout.intValue());
		Date nextTryDate = calendar.getTime();

		Backup backup = backupEntity.getBackup();
		backupScheduler.schedule(backup, new IP(backupEntity.getIp()), nextTryDate);

		backupEntity.setDateTime(nextTryDate);
		backupEntity.increaseTryCount();
		backupRepository.save(backupEntity);

		return true;
	}
}
