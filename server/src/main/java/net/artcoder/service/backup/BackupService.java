package net.artcoder.service.backup;

import net.artcoder.domain.Backup;
import net.artcoder.domain.IP;
import net.artcoder.domain.exception.*;
import net.artcoder.dto.NewBackupCommand;

import java.util.List;

public interface BackupService {

	String createBackup(NewBackupCommand dto);

	void enableBackup(String backupId) throws BackupNotFoundException, AlreadyActiveException;

	void disableBackup(String backupId) throws BackupNotFoundException, AlreadyDisabledException;

	void deleteBackup(String backupId) throws BackupNotFoundException;

	Backup findBackup(String backupId) throws BackupNotFoundException;

	List<Backup> findBackups(IP ip) throws MachineNotFoundException;

	Backup getNextBackupByIpAddress(IP address) throws FullSlotException;
}
