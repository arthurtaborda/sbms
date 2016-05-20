package net.artcoder.service.backup;

import net.artcoder.domain.Backup;
import net.artcoder.domain.IP;
import net.artcoder.domain.exception.AlreadyDisabledException;
import net.artcoder.domain.exception.BackupNotFoundException;
import net.artcoder.domain.exception.AlreadyActiveException;
import net.artcoder.service.backup.task.SendTimeoutTask;

import java.util.Date;

public interface BackupScheduler {
	void schedule(Backup backup, IP address, Date date);

	void enableBackup(String backupId) throws AlreadyActiveException, BackupNotFoundException;

	void disableBackup(String backupId) throws BackupNotFoundException, AlreadyDisabledException;

	void deleteBackup(String backupId) throws BackupNotFoundException;

	boolean hasTimeOut(String backupId);

	boolean hasSchedule(String backupId);

	void cancelTimeout(String backupId);

	void startTimeoutForBackup(Integer timeout, String backupId, SendTimeoutTask sendTimeoutTask);
}
