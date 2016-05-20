package net.artcoder.domain;

import net.artcoder.domain.exception.FullSlotException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConcurrentBackupQueueTest {

	private Backup backup;
	private Backup backup2;
	private Backup backup3;
	private IP ip;
	private IP ip2;

	@Before
	public void setUp() throws Exception {
		backup = new Backup("id", "s1", "sd1", "d1", "dd1");
		backup2 = new Backup("id2", "s2", "sd2", "d2", "dd2");
		backup3 = new Backup("id3", "s3", "sd3", "d3", "dd3");
		ip = new IP("255.255.255.255");
		ip2 = new IP("1.1.1.1");
	}

	@Test
	public void testAdd() {
		BackupQueue queue = new ConcurrentBackupQueue(1);
		queue.add(ip, backup);
		queue.add(ip, backup2);
		queue.add(ip, backup);


		assertTrue(queue.size() == 1);
		assertTrue(queue.contains(ip));
	}

	@Test
	public void testPoll() throws FullSlotException {
		BackupQueue queue = new ConcurrentBackupQueue(3);
		queue.add(ip, backup);
		queue.add(ip2, backup2);
		queue.add(ip2, backup3);

		Backup b2 = queue.poll(ip2);
		Backup b1 = queue.poll(ip);
		Backup b3 = queue.poll(ip2);

		assertEquals(backup, b1);
		assertEquals(backup2, b2);
		assertEquals(backup3, b3);
	}

	@Test
	public void testRemove() {
		BackupQueue queue = new ConcurrentBackupQueue(1);
		queue.add(ip, backup);
		queue.remove(ip, backup.getId());

		assertEquals(new Integer(0), queue.size());
		assertFalse(queue.contains(ip));
	}

	@Test
	public void testSlotFull() {
		BackupQueue queue = new ConcurrentBackupQueue(2);
		queue.add(ip, backup);
		queue.add(ip2, backup2);
		queue.add(ip2, backup3);

		boolean poll1ThrewException = false;
		Backup b1 = null;
		try {
			b1 = queue.poll(ip);
		} catch (FullSlotException e) {
			poll1ThrewException = true;
		}

		boolean poll2ThrewException = false;
		Backup b2 = null;
		try {
			b2 = queue.poll(ip2);
			assertSame(backup2, b2);
		} catch (FullSlotException e) {
			poll2ThrewException = true;
		}

		boolean poll3ThrewFullSlotException = false;
		Backup b3 = null;
		try {
			b3 = queue.poll(ip2);
		} catch (FullSlotException e) {
			poll3ThrewFullSlotException = true;
		}

		assertSame(backup, b1);
		assertFalse("Should not have thrown exception", poll1ThrewException);

		assertSame(backup2, b2);
		assertFalse("Should not have thrown exception", poll2ThrewException);

		assertNull(b3);
		assertTrue("Should have thrown full slot exception", poll3ThrewFullSlotException);
	}


	@Test
	public void testFreeSlot() throws FullSlotException {
		BackupQueue queue = new ConcurrentBackupQueue(1);
		queue.add(ip, backup);

		Integer freeSlotsBeforePoll = queue.slotsFreeSize();
		queue.poll(ip);
		Integer freeSlotsAfterPoll = queue.slotsFreeSize();
		queue.freeSlot(backup.getId());
		Integer freeSlotsAfterFree = queue.slotsFreeSize();

		assertEquals(new Integer(1), freeSlotsBeforePoll);
		assertEquals(new Integer(0), freeSlotsAfterPoll);
		assertEquals(new Integer(1), freeSlotsAfterFree);
	}


}