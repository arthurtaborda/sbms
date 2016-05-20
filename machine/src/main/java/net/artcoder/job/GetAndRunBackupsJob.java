package net.artcoder.job;

import net.artcoder.domain.Backup;
import net.artcoder.domain.BackupExecution;
import net.artcoder.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GetAndRunBackupsJob {

	private Backup backupExecuting;
	private ServerService serverService;

	@Autowired
	public GetAndRunBackupsJob(ServerService serverService) {
		this.serverService = serverService;
	}

	@Scheduled(fixedRate = 1000)
	public void getAndRunBackups() {
		boolean isExecutingBackup = backupExecuting != null;
		if (isExecutingBackup) {
			BackupExecution execution = backupExecuting.getExecutions().poll();
			if (execution != null) {
				BackupExecution.Status status = execution.getStatus();
				try {
					serverService.sendStatusToServer(execution, backupExecuting.getId());
				} catch (IOException e) {
					e.printStackTrace();
				}

				if(status == BackupExecution.Status.DONE || status == BackupExecution.Status.ERROR) {
					backupExecuting = null;
				}
			}
		} else {
			Backup backup = serverService.getBackup();

			if(backup != null) {
				backupExecuting = backup;
				backup.start();
			}
		}
	}

}
