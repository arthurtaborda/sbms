package net.artcoder.service;

import net.artcoder.domain.Backup;
import net.artcoder.domain.BackupExecution;

import java.io.IOException;

public interface ServerService {
	Backup getBackup();

	void sendStatusToServer(BackupExecution backupStatus, String backupId) throws IOException;
}
