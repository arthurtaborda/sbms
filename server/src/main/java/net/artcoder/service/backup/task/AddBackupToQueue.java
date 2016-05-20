package net.artcoder.service.backup.task;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.artcoder.domain.Backup;
import net.artcoder.domain.BackupQueue;
import net.artcoder.domain.IP;
import net.artcoder.persistence.repository.BackupRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class AddBackupToQueue implements Runnable {

	private Backup backup;
	private IP address;
	private BackupQueue backupQueue;
	private BackupRepository backupRepository;

	@Getter
	private boolean executed = false;


	public AddBackupToQueue(Backup backup, IP address, BackupQueue backupQueue,
							BackupRepository backupRepository) {
		this.backup = backup;
		this.address = address;
		this.backupQueue = backupQueue;
		this.backupRepository = backupRepository;
	}


	@Override
	@Transactional
	public void run() {
		log.debug("Adding backup to queue: " + backup.getId());

		backupQueue.add(address, backup);
		backupRepository.queue(backup.getId());
		executed = true;
	}
}
