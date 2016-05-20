package net.artcoder.service.backup.task;

import net.artcoder.domain.Backup;
import net.artcoder.domain.BackupQueue;
import net.artcoder.domain.ConcurrentBackupQueue;
import net.artcoder.domain.IP;
import net.artcoder.persistence.repository.BackupRepository;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class AddBackupToQueueTest {

	@Test
	public void testAdd() throws Exception {
		Backup backup = new Backup("id", "s1", "sd1", "d1", "dd1");
		BackupQueue queue = new ConcurrentBackupQueue(1);
		IP address = new IP("1.1.1.1");
		AddBackupToQueue task = new AddBackupToQueue(backup, address, queue, mock(BackupRepository.class));

		task.run();

		assertTrue(queue.size() == 1);
		assertTrue(queue.contains(address));
	}
}