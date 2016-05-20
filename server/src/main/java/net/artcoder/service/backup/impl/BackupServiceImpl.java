package net.artcoder.service.backup.impl;

import lombok.extern.slf4j.Slf4j;
import net.artcoder.domain.Backup;
import net.artcoder.domain.BackupQueue;
import net.artcoder.domain.BackupState;
import net.artcoder.domain.IP;
import net.artcoder.domain.exception.*;
import net.artcoder.dto.NewBackupCommand;
import net.artcoder.persistence.entity.BackupEntity;
import net.artcoder.persistence.repository.BackupRepository;
import net.artcoder.service.backup.BackupScheduler;
import net.artcoder.service.backup.BackupService;
import net.artcoder.service.backup.BackupStatusService;
import net.artcoder.service.backup.task.AddBackupToQueue;
import net.artcoder.service.backup.task.SendTimeoutTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BackupServiceImpl implements BackupService {

	private Integer timeout; //timeout to execute the whole backup

	private BackupQueue backupQueue;
	private BackupRepository backupRepository;
	private BackupScheduler backupScheduler;
	private BackupStatusService backupStatusService;

	@Autowired
	public BackupServiceImpl(@Qualifier("timeout") Integer timeout,
							 BackupRepository backupRepository, BackupQueue backupQueue,
							 BackupScheduler backupScheduler, BackupStatusService backupStatusService) {
		this.timeout = timeout;
		this.backupQueue = backupQueue;
		this.backupRepository = backupRepository;
		this.backupScheduler = backupScheduler;
		this.backupStatusService = backupStatusService;
	}

	@Transactional
	@PostConstruct
	public void init() throws InvalidIPAddressException {
		Set<BackupEntity> backups = backupRepository.findEnabledBackups();
		for (BackupEntity be : backups) {
			Backup backup = be.getBackup();

			boolean mustExecuteImmediately = be.getDateTime().before(new Date());

			IP address = new IP(be.getIp());
			if (mustExecuteImmediately) {
				runNow(address, backup);
			} else {
				backupScheduler.schedule(backup, address, be.getDateTime());
			}
		}
	}

	@Override
	public String createBackup(NewBackupCommand dto) {
		IP address = dto.getAddress();

		BackupState state = BackupState.SCHEDULED;
		LocalDateTime dateAndTime = dto.getDateAndTime();
		boolean mustExecuteImmediately = dateAndTime == null;
		Backup backup = dto.getBackup();

		if(mustExecuteImmediately) {
			state = BackupState.QUEUED;
			runNow(address, backup);
		} else {
			Date date = Date.from(dateAndTime.atZone(ZoneId.systemDefault()).toInstant());
			backupScheduler.schedule(backup, address, date);
		}

		saveToDatabase(address, dto, state);

		return backup.getId();
	}

	private void runNow(IP address, Backup backup) {
		AddBackupToQueue task = new AddBackupToQueue(backup, address, backupQueue, backupRepository);
		task.run();
	}

	private void saveToDatabase(IP address, NewBackupCommand dto, BackupState state) {
		Backup backup = dto.getBackup();
		LocalDateTime dateTime = dto.getDateAndTime();
		if(dateTime == null)
			dateTime = LocalDateTime.now();
		Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
		String source = backup.getSource();
		String destination = backup.getDestination();
		String sourceDomain = backup.getSourceDomain();
		String destinationDomain = backup.getDestinationDomain();

		BackupEntity entity = new BackupEntity(backup.getId(), source, sourceDomain, destination,
				destinationDomain, date, address.toString());
		entity.setState(state);
		entity.setSourceUser(backup.getSourceUser());
		entity.setSourcePass(backup.getSourcePass());
		entity.setDestinationUser(backup.getDestinationUser());
		entity.setDestinationPass(backup.getDestinationPass());
		entity.setMaximumReschedules(backup.getMaximumReschedules());
		entity.setRescheduleTimeout(backup.getRescheduleTimeout());

		backupRepository.save(entity);
	}

	@Override
	@Transactional
	public void enableBackup(String backupId) throws BackupNotFoundException, AlreadyActiveException {
		try {
			backupScheduler.enableBackup(backupId);
			backupRepository.schedule(backupId);
		} catch (BackupNotFoundException e) {
			BackupEntity backupEntity = backupRepository.findOne(backupId);

			if(backupEntity == null) {
				throw new BackupNotFoundException();
			}

			IP address = new IP(backupEntity.getIp());
			Backup backup = backupEntity.getBackup();

			boolean isFuture = backupEntity.getDateTime().after(new Date());
			boolean isDisabled = backupEntity.getState().equals(BackupState.DISABLED);
			boolean inQueue = backupQueue.contains(address, backupId);
			if(isDisabled && !inQueue) {
				if (isFuture) {
					backupScheduler.schedule(backup, address, backupEntity.getDateTime());
				} else {
					runNow(address, backup);
				}
			} else {
				throw new AlreadyActiveException("Backup must be disabled to be enabled");
			}
		}
	}

	@Override
	@Transactional
	public void disableBackup(String backupId) throws BackupNotFoundException, AlreadyDisabledException {
		try {
			backupScheduler.disableBackup(backupId);
		} catch (BackupNotFoundException ignored) {
		}
		BackupEntity backupEntity = backupRepository.findOne(backupId);

		if (backupEntity == null) {
			throw new BackupNotFoundException();
		}

		IP address = new IP(backupEntity.getIp());
		try {
			backupQueue.remove(address, backupId);
		} catch (NoSuchElementException ignored) {
		}

		backupRepository.disable(backupId);
	}

	@Override
	@Transactional
	public void deleteBackup(String backupId) throws BackupNotFoundException {
		try {
			backupScheduler.deleteBackup(backupId);
		} catch (BackupNotFoundException e) {
			BackupEntity backupEntity = backupRepository.findOne(backupId);

			if (backupEntity == null) {
				throw new BackupNotFoundException();
			}

			IP address = new IP(backupEntity.getIp());
			try {
				backupQueue.remove(address, backupId);
			} catch (NoSuchElementException ignored) {
			}
		}

		backupRepository.delete(backupId);
	}

	@Override
	public Backup findBackup(String backupId) throws BackupNotFoundException {
		BackupEntity backupEntity = backupRepository.findOne(backupId);

		if(backupEntity == null) {
			throw new BackupNotFoundException();
		}

		return backupEntity.getBackup();
	}


	@Override
	public List<Backup> findBackups(IP ip) throws MachineNotFoundException {
		Set<BackupEntity> backupEntities = backupRepository.findByIp(ip.toString());

		if (backupEntities == null) {
			throw new MachineNotFoundException("Machine " + ip.toString() + " could not be found");
		}

		return backupEntities
				.stream()
				.map(BackupEntity::getBackup)
				.collect(Collectors.toList());
	}

	@Override
	public Backup getNextBackupByIpAddress(IP address) throws FullSlotException {
		Backup backup = backupQueue.poll(address);

		if (backup != null) {
			SendTimeoutTask sendTimeoutTask = new SendTimeoutTask(backup.getId(), backupStatusService);
			backupScheduler.startTimeoutForBackup(timeout, backup.getId(), sendTimeoutTask);
		}

		return backup;
	}
}
