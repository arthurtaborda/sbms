package net.artcoder.service.backup.task;

import lombok.extern.slf4j.Slf4j;
import net.artcoder.domain.BackupExecutionStatus;
import net.artcoder.domain.exception.BackupNotFoundException;
import net.artcoder.service.backup.BackupStatusService;

import java.util.Date;

@Slf4j
public class SendTimeoutTask implements Runnable {

	private String backupId;
	private BackupStatusService backupStatusService;

	public SendTimeoutTask(String backupId, BackupStatusService backupStatusService) {
		this.backupId = backupId;
		this.backupStatusService = backupStatusService;
	}


	@Override
	public void run() {
		log.debug("Timeout for backup: " + backupId);

		try {
			backupStatusService.backupStatus(backupId, new Date(), BackupExecutionStatus.TIMEOUT, "Request timeout");
		} catch (BackupNotFoundException e) {
			e.printStackTrace();
		}
	}
}
