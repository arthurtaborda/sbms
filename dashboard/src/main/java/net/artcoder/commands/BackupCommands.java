package net.artcoder.commands;

import net.artcoder.rest.BackupRestClient;
import net.artcoder.rest.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BackupCommands implements CommandMarker {

	private BackupRestClient backupRestClient;

	@Autowired
	public BackupCommands(BackupRestClient backupRestClient) {
		this.backupRestClient = backupRestClient;
	}

	@CliCommand(value = "backup create", help = "Creates a new backup")
	public String backupCreate(
			@CliOption(key = {"ip"}, mandatory = true)
			final String ip,
			@CliOption(key = {"source"}, mandatory = true)
			final String source,
			@CliOption(key = {"destination"}, mandatory = true)
			final String destination,
			@CliOption(key = {"sourceDomain"}, mandatory = true)
			final String sourceDomain,
			@CliOption(key = {"sourceUser"})
			final String sourceUser,
			@CliOption(key = {"sourcePass"})
			final String sourcePass,
			@CliOption(key = {"destinationDomain"}, mandatory = true)
			final String destinationDomain,
			@CliOption(key = {"destinationUser"})
			final String destinationUser,
			@CliOption(key = {"destinationPass"})
			final String destinationPass,
			@CliOption(key = {"rescheduleTimeout"})
			final Long rescheduleTimeout,
			@CliOption(key = {"maximumReschedules"})
			final Integer maximumReschedules,
			@CliOption(key = {"datetime"})
			final String datetime
	) {
		try {

			String id = backupRestClient.backupCreate(ip,
					source,
					destination,
					sourceDomain,
					sourceUser,
					sourcePass,
					destinationDomain,
					destinationUser,
					destinationPass,
					rescheduleTimeout,
					maximumReschedules,
					datetime);

			return "Backup created successfully. This is the id:" + id;
		} catch (IOException e) {
			return "An error occurred: " + e.getMessage();
		}
	}

	@CliCommand(value = "backup enable", help = "Enable a scheduled backup that has been disabled")
	public String backupEnable(
			@CliOption(key = {"id"}, mandatory = true)
			final String id
	) {
		try {
			backupRestClient.backupEnable(id);

			return "Backup enabled successfully.";
		} catch (IOException e) {
			return "An error occurred: " + e.getMessage();
		}
	}

	@CliCommand(value = "backup disable", help = "Disables a scheduled backup")
	public String backupDisable(
			@CliOption(key = {"id"}, mandatory = true)
			final String id
	) {
		try {
			backupRestClient.backupDisable(id);

			return "Backup disabled successfully.";
		} catch (IOException e) {
			return "An error occurred: " + e.getMessage();
		}
	}

	@CliCommand(value = "backup delete", help = "Deletes a scheduled backup")
	public String backupDelete(
			@CliOption(key = {"id"}, mandatory = true)
			final String id
	) {
		try {
			backupRestClient.backupDelete(id);

			return "Backup deleted successfully.";
		} catch (IOException e) {
			return "An error occurred: " + e.getMessage();
		}
	}

	@CliCommand(value = "backup show", help = "Show one backup given his id")
	public String showBackupDetails(
			@CliOption(key = {"id"}, mandatory = true)
			final String id
	) {
		try {
			String body = backupRestClient.getBackupDetails(id);

			return JsonUtil.prettify(body);
		} catch (IOException e) {
			return "An error occurred: " + e.getMessage();
		}
	}

	@CliCommand(value = "backup list", help = "List all backups from a specific machine")
	public String showBackupsFromMachine(
			@CliOption(key = {"ip"}, mandatory = true)
			final String ip
	) {
		try {
			String body = backupRestClient.getBackupsFromMachine(ip);

			return JsonUtil.prettify(body);
		} catch (IOException e) {
			return "An error occurred: " + e.getMessage();
		}
	}
}
