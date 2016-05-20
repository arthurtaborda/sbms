package net.artcoder.service.backup;

import net.artcoder.domain.BackupExecutionStatus;
import net.artcoder.domain.exception.BackupNotFoundException;

import java.util.Date;

public interface BackupStatusService {

	void backupStatus(String backupId, Date date, BackupExecutionStatus st, String message) throws
			BackupNotFoundException;
}
