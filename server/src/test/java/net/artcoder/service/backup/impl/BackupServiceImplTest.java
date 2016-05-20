package net.artcoder.service.backup.impl;

import com.google.common.collect.Sets;
import net.artcoder.domain.Backup;
import net.artcoder.domain.BackupQueue;
import net.artcoder.domain.ConcurrentBackupQueue;
import net.artcoder.domain.IP;
import net.artcoder.domain.exception.AlreadyActiveException;
import net.artcoder.domain.exception.BackupNotFoundException;
import net.artcoder.persistence.entity.BackupEntity;
import net.artcoder.persistence.repository.BackupRepository;
import net.artcoder.service.backup.BackupStatusService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Future;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BackupServiceImplTest {

	IP ip1;
	IP ip2;

	BackupServiceImpl backupService;
	private BackupQueue queue;
	private BackupSchedulerImpl scheduler;

	String pastIP1 = "pastIP1";
	String pastIP2 = "pastIP2";
	String futureIP1 = "futureIP1";
	String futureIP2 = "futureIP2";
	private BackupRepository repository;
	private BackupEntity futBkp1;

	@Before
	public void setUp() throws Exception {
		ip1 = new IP("1.1.1.1");
		ip2 = new IP("2.2.2.2");

		repository = mock(BackupRepository.class);
		when(repository.findEnabledBackups()).thenReturn(getEnabledBackups());

		queue = new ConcurrentBackupQueue(1);
		BackupStatusService statusService = (backupId, date, st, message) -> {
		};
		scheduler = new BackupSchedulerImpl(queue, new DefaultManagedTaskScheduler(), repository);
		backupService = new BackupServiceImpl(30000, repository, queue, scheduler, statusService);
	}



	public HashSet<BackupEntity> getEnabledBackups() {
		Calendar future = Calendar.getInstance();
		future.add(Calendar.HOUR, 1);
		Calendar past = Calendar.getInstance();
		past.add(Calendar.HOUR, -1);

		pastIP1 = "pastIP1";
		pastIP2 = "pastIP2";
		futureIP1 = "futureIP1";
		futureIP2 = "futureIP2";
		Date futDate = future.getTime();
		Date pastDate = past.getTime();
		futBkp1 = new BackupEntity(futureIP1, "s1", "sd1", "d1", "dd1", futDate, ip1.toString());
		BackupEntity pastBkp1 = new BackupEntity(pastIP1, "s2", "sd2", "d2", "dd2", pastDate, ip1.toString());
		BackupEntity futBkp2 = new BackupEntity(futureIP2, "s3", "sd3", "d3", "dd3", futDate, ip2.toString());
		BackupEntity pastBkp2 = new BackupEntity(pastIP2, "s4", "sd4", "d4", "dd4", pastDate, ip2.toString());

		return Sets.newHashSet(futBkp1, pastBkp1, futBkp2, pastBkp2);
	}

	@Test
	public void testInit() throws Exception {
		backupService.init();
		//init 2 scheduled and 2 to execute immediately,
		//from different ip addresses

		assertEquals(new Integer(2), queue.size()); //2 ip
		assertEquals(new Integer(1), queue.size(ip1)); //1 from ip1
		assertEquals(new Integer(1), queue.size(ip2)); //1 from ip2

		assertEquals(2, scheduler.backupsScheduledTasks.size());
		assertNotNull(scheduler.backupsScheduledTasks.get(futureIP1));
		assertNotNull(scheduler.backupsScheduledTasks.get(futureIP2));
	}

	@Test
	public void testDisableAndEnableBackup() throws Exception {
		backupService.init();
		when(repository.findOne(futureIP1)).thenReturn(futBkp1);

		boolean wasDeactivatedBefore = !scheduler.backupsScheduledTasks.get(futureIP1).isActive();
		backupService.disableBackup(futureIP1);
		boolean isDeactivatedAfter = !scheduler.backupsScheduledTasks.get(futureIP1).isActive();
		backupService.enableBackup(futureIP1);
		boolean isActiveAfter = scheduler.backupsScheduledTasks.get(futureIP1).isActive();

		assertFalse(wasDeactivatedBefore);
		assertTrue(isDeactivatedAfter);
		assertTrue(isActiveAfter);
	}

	@Test(expected = BackupNotFoundException.class)
	public void testEnableBackupThatDoesNotExist() throws Exception {
		backupService.enableBackup("nonExistentBackup");
	}

	@Test(expected = BackupNotFoundException.class)
	public void testEnableFutureBackupThatIsNotInScheduleButIsInDatabase() throws Exception {
		when(repository.findOne(futureIP1)).thenReturn(futBkp1);
		backupService.enableBackup("nonExistentBackup");
	}

	@Test(expected = AlreadyActiveException.class)
	public void testEnableBackupThatIsAlreadyActive() throws Exception {
		backupService.init();
		backupService.enableBackup(futureIP1);
	}

	@Test(expected = BackupNotFoundException.class)
	public void testDisableBackupThatDoesNotExist() throws Exception {
		backupService.disableBackup("nonExistentBackup");
	}

	@Test(expected = BackupNotFoundException.class)
	public void testDeleteBackupThatDoesNotExist() throws Exception {
		backupService.deleteBackup("nonExistentBackup");
	}

	@Test(expected = BackupNotFoundException.class)
	public void testDeleteBackupWillCancelTimeout() throws Exception {
		backupService.init();
		Backup nextBackup = backupService.getNextBackupByIpAddress(ip1);
		Future timeoutBeforeDelete = scheduler.backupsTimeout.get(nextBackup.getId());
		backupService.deleteBackup(nextBackup.getId());
		Future timeoutAfterDelete = scheduler.backupsTimeout.get(nextBackup.getId());
		boolean timeoutCancelledAfterDelete = timeoutBeforeDelete.isCancelled();

		assertNotNull(timeoutBeforeDelete);
		assertNull(timeoutAfterDelete);
		assertTrue(timeoutCancelledAfterDelete);
	}

}