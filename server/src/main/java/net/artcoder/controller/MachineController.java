package net.artcoder.controller;

import net.artcoder.domain.Backup;
import net.artcoder.domain.IP;
import net.artcoder.domain.exception.MachineNotFoundException;
import net.artcoder.domain.exception.InvalidIPAddressException;
import net.artcoder.dto.NewMachineCommand;
import net.artcoder.service.backup.BackupService;
import net.artcoder.service.machine.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/machines", produces = "application/json")
public class MachineController {

	private BackupService backupService;
	private MachineService machineService;

	@Autowired
	public MachineController(BackupService backupService, MachineService machineService) {
		this.backupService = backupService;
		this.machineService = machineService;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity addMachine(@RequestParam("ip")
											String ip,
									 @RequestParam("password")
											String password) throws InvalidIPAddressException {

		IP address = new IP(ip);

		machineService.createMachine(new NewMachineCommand(address, password));

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "/{ip}/backups", method = RequestMethod.GET)
	public ResponseEntity getBackupDetails(@PathVariable("ip") String ip) throws InvalidIPAddressException, MachineNotFoundException {
		List<Backup> backups = backupService.findBackups(new IP(ip));

		return ResponseEntity.ok().body(backups);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity getBackupDetails() throws InvalidIPAddressException, MachineNotFoundException {
		List<String> machines = machineService.findMachines();

		return ResponseEntity.ok().body(machines);
	}
}
