package net.artcoder.controller;

import net.artcoder.domain.Backup;
import net.artcoder.domain.BackupExecutionStatus;
import net.artcoder.domain.IP;
import net.artcoder.domain.exception.*;
import net.artcoder.dto.ErrorDto;
import net.artcoder.dto.ID;
import net.artcoder.dto.NewBackupCommand;
import net.artcoder.service.backup.BackupService;
import net.artcoder.service.backup.BackupStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/backups", produces = "application/json")
public class BackupController {

	private BackupService backupService;
	private BackupStatusService backupStatusService;

	@Autowired
	public BackupController(BackupService backupService, BackupStatusService backupStatusService) {
		this.backupService = backupService;
		this.backupStatusService = backupStatusService;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity postBackup(@RequestParam("ip")
											 String ip,
									 @RequestParam("source")
											 String source,
									 @RequestParam("destination")
											 String destination,
									 @RequestParam(value = "sourceDomain")
											 String sourceDomain,
									 @RequestParam(value = "sourceUser", required = false)
											 String sourceUser,
									 @RequestParam(value = "sourcePass", required = false)
											 String sourcePass,
									 @RequestParam(value = "destinationDomain")
											 String destinationDomain,
									 @RequestParam(value = "destinationUser", required = false)
											 String destinationUser,
									 @RequestParam(value = "destinationPass", required = false)
											 String destinationPass,
									 @RequestParam(value = "rescheduleTimeout", required = false)
											 Long rescheduleTimeout,
									 @RequestParam(value = "maximumReschedules", required = false)
											 Integer maximumReschedules,
									 @RequestParam(value = "datetime", required = false)
									 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
											 LocalDateTime dateAndTime) throws InvalidIPAddressException {

		if (dateAndTime != null && dateAndTime.isBefore(LocalDateTime.now())) {
			return ResponseEntity.badRequest().body(new ErrorDto(400, "This date has already passed"));
		}

		Backup backup = new Backup(UUID.randomUUID().toString(), source, sourceDomain, destination, destinationDomain);
		if (StringUtils.hasText(sourceUser) && StringUtils.hasText(sourcePass)) {
			backup.setSourceAccessCredentials(sourceUser, sourcePass);
		}
		if (StringUtils.hasText(destinationUser) && StringUtils.hasText(destinationPass)) {
			backup.setDestinationAccessCredentials(destinationUser, destinationPass);
		}

		if (rescheduleTimeout != null) {
			try {
				if (maximumReschedules == null)
					maximumReschedules = 3;
				backup.setTimeout(rescheduleTimeout, maximumReschedules);
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest().body(new ErrorDto(400, e.getMessage()));
			}
		}

		IP address;
		try {
			address = new IP(ip);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new ErrorDto(400, "IP address is invalid"));
		}

		NewBackupCommand dto = new NewBackupCommand(address, backup);
		dto.setDateAndTime(dateAndTime);

		String id = backupService.createBackup(dto);

		return ResponseEntity.status(HttpStatus.CREATED).body(new ID(id));
	}


	@PreAuthorize("hasRole('ROLE_MACHINE') and principal.username == #ip")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity getAvailableBackup(@RequestParam("ip") String ip) throws FullSlotException, InvalidIPAddressException {
		ResponseEntity<Void> noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();

		Backup backup = backupService.getNextBackupByIpAddress(new IP(ip));
		if (backup == null) {
			return noContent;
		}

		return ResponseEntity.status(HttpStatus.OK).body(backup);
	}

	@PreAuthorize("hasRole('ROLE_MACHINE')")
	@RequestMapping(value = "/{backupId}/status", method = RequestMethod.POST)
	public ResponseEntity backupStatus(@PathVariable("backupId")
											   String backupId,
									   @RequestParam("status")
											   String status,
									   @RequestParam("datetime")
									   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
											   Date date,
									   @RequestParam(value = "message", required = false)
											   String message) throws
			BackupNotFoundException {

		BackupExecutionStatus st;
		try {
			st = BackupExecutionStatus.valueOf(status);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new ErrorDto(400, "Invalid status"));
		}

		backupStatusService.backupStatus(backupId, date, st, message);

		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{backupId}/enable", method = RequestMethod.POST)
	public ResponseEntity enableBackup(@PathVariable("backupId") String backupId) throws BackupNotFoundException, AlreadyActiveException {
		backupService.enableBackup(backupId);

		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{backupId}/disable", method = RequestMethod.POST)
	public ResponseEntity disableBackup(@PathVariable("backupId") String backupId) throws BackupNotFoundException, AlreadyDisabledException {
		backupService.disableBackup(backupId);

		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{backupId}", method = RequestMethod.DELETE)
	public ResponseEntity deleteBackup(@PathVariable("backupId") String backupId) throws BackupNotFoundException {
		backupService.deleteBackup(backupId);

		return ResponseEntity.ok().build();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{backupId}", method = RequestMethod.GET)
	public ResponseEntity getBackupDetails(@PathVariable("backupId") String backupId) throws BackupNotFoundException {
		Backup backup = backupService.findBackup(backupId);

		return ResponseEntity.ok().body(backup);
	}
}
