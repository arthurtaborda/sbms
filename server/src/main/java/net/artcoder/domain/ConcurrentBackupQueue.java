package net.artcoder.domain;

import com.google.common.collect.Sets;
import net.artcoder.domain.exception.FullSlotException;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentBackupQueue implements BackupQueue {

	private Integer slotSize;
	private final Set<Backup> slot = Sets.newConcurrentHashSet();
	private final Map<IP, Queue<Backup>> backupsMap = new ConcurrentHashMap<>();

	public ConcurrentBackupQueue(Integer slotSize) {
		this.slotSize = slotSize;
	}

	@Override
	public void add(IP address, Backup backup) {
		Queue<Backup> backups = backupsMap.get(address);

		if (backups == null) {
			backups = new ConcurrentLinkedQueue<>();
			backupsMap.put(address, backups);
		}

		backups.add(backup);
	}

	@Override
	public Backup remove(IP address, String backupId) {
		Queue<Backup> backups = backupsMap.get(address);

		if (backups != null) {
			for (Backup backup : backups) {
				if (backup.getId().equals(backupId)) {
					backups.remove(backup);
					cleanIfEmpty(address);
					return backup;
				}
			}
		}

		throw new NoSuchElementException("Could not find " + backupId);
	}

	private void cleanIfEmpty(IP address) {
		Queue<Backup> backups = backupsMap.get(address);
		if(backups != null && backups.isEmpty()) {
			backupsMap.remove(address);
		}
	}

	@Override
	public Backup poll(IP address) throws FullSlotException {
		Queue<Backup> backups;
		if(slot.size() >= slotSize) {
			throw new FullSlotException();
		}

		backups = backupsMap.get(address);

		Backup next = null;
		if(backups != null && backups.size() > 0) {
			next = backups.poll();
			slot.add(next);
			cleanIfEmpty(address);
		}

		return next;
	}

	@Override
	public void freeSlot(String backupId) {
		slot.stream()
				.filter(backup -> backup.getId().equals(backupId))
				.forEach(slot::remove);
	}

	@Override
	public Integer size() {
		return backupsMap.size();
	}

	@Override
	public Integer size(IP address) {
		Queue<Backup> backups = backupsMap.get(address);
		if (backups != null && !backups.isEmpty()) {
			return backups.size();
		}

		return 0;
	}

	@Override
	public boolean contains(IP ip) {
		return backupsMap.keySet().contains(ip);
	}

	@Override
	public boolean contains(IP address, String backupId) {
		Queue<Backup> backups = backupsMap.get(address);
		if (backups != null && !backups.isEmpty()) {
			for (Backup backup : backups) {
				if(backup.getId().equals(backupId))
					return true;
			}
		}

		return false;
	}

	@Override
	public Integer slotsFreeSize() {
		return slotSize - slot.size();
	}

	Map<IP, Queue<Backup>> getMap() {
		return backupsMap;
	}
}
