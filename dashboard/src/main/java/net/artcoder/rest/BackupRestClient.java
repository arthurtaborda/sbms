package net.artcoder.rest;

import java.io.IOException;

public interface BackupRestClient {

	String backupCreate(String ip, String source, String destination, String sourceDomain, String sourceUser,
						String sourcePass, String destinationDomain, String destinationUser,
						String destinationPass, Long rescheduleTimeout, Integer maximumReschedules, String datetime) throws IOException;

	void backupEnable(String ip) throws IOException;

	void backupDisable(String ip) throws IOException;

	void backupDelete(String ip) throws IOException;

	String getBackupDetails(String backupId) throws IOException;

	String getBackupsFromMachine(String ip) throws IOException;
}
