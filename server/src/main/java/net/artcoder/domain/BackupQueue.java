package net.artcoder.domain;

import net.artcoder.domain.exception.FullSlotException;

public interface BackupQueue {
	void add(IP address, Backup backup);

	Backup remove(IP address, String backupId);

	Backup poll(IP address) throws FullSlotException;

	void freeSlot(String backupId);

	Integer size();

	Integer size(IP address);

	boolean contains(IP ip);

	boolean contains(IP address, String backupId);

	Integer slotsFreeSize();
}
