package net.artcoder.service.backup.impl;

import net.artcoder.TestUtil;
import net.artcoder.domain.*;
import net.artcoder.domain.exception.BackupNotFoundException;
import net.artcoder.domain.exception.FullSlotException;
import net.artcoder.persistence.entity.BackupEntity;
import net.artcoder.persistence.repository.BackupExecutionRepository;
import net.artcoder.persistence.repository.BackupRepository;
import net.artcoder.service.backup.BackupScheduler;
import net.artcoder.service.backup.task.SendTimeoutTask;
import org.junit.Before;
import org.junit.Test;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BackupStatusServiceImplTest {

	IP ip1;
	Backup backup;

	BackupStatusServiceImpl service;
	private BackupQueue queue;
	private BackupScheduler scheduler;
	private BackupRepository repository;
	private BackupEntity backupEntity;

	@Before
	public void setUp() throws Exception {
		ip1 = TestUtil.getIP("1");
		backup = TestUtil.getBackup(1);

		repository = mock(BackupRepository.class);
		backupEntity = TestUtil.getBackupEntity(backup.getId());
		when(repository.findOne(anyString())).thenReturn(backupEntity);

		queue = new ConcurrentBackupQueue(1);
		scheduler = new BackupSchedulerImpl(queue, new DefaultManagedTaskScheduler(), repository);
		BackupExecutionRepository executionRepository = mock(BackupExecutionRepository.class);
		service = new BackupStatusServiceImpl(repository, queue, scheduler, executionRepository);
	}

	@Test(expected = BackupNotFoundException.class)
	public void testThrowsExceptionIfBackupDoesNotExist() throws Exception {
		when(repository.findOne(anyString())).thenReturn(null);

		service.backupStatus("id", new Date(), BackupExecutionStatus.ERROR, "");
	}

	@Test
	public void testFreeSlotWhenDone() throws Exception {
		testFreeSlotOnEvent(ip1, backup, backup.getId(), BackupExecutionStatus.DONE);
	}

	@Test
	public void testFreeSlotWhenTimeout() throws Exception {
		testFreeSlotOnEvent(ip1, backup, backup.getId(), BackupExecutionStatus.TIMEOUT);
	}

	@Test
	public void testFreeSlotWhenError() throws Exception {
		testFreeSlotOnEvent(ip1, backup, backup.getId(), BackupExecutionStatus.ERROR);
	}

	private void testFreeSlotOnEvent(IP ip1, Backup backup, String id2, BackupExecutionStatus status) throws FullSlotException, BackupNotFoundException {
		queue.add(ip1, backup);
		queue.poll(ip1);

		Integer freeSlotsBeforeEvent = queue.slotsFreeSize();
		service.backupStatus(id2, new Date(), status, "");
		Integer freeSlotsAfterEvent = queue.slotsFreeSize();

		assertEquals(new Integer(0), freeSlotsBeforeEvent);
		assertEquals(new Integer(1), freeSlotsAfterEvent);
	}

	@Test
	public void testCancelTimeoutWhenDone() throws Exception {
		testCancelTimeoutOnEvent(backup, backup.getId(), BackupExecutionStatus.DONE);
	}

	@Test
	public void testCancelTimeoutWhenTimeout() throws Exception {
		testCancelTimeoutOnEvent(backup, backup.getId(), BackupExecutionStatus.TIMEOUT);
	}

	@Test
	public void testCancelTimeoutWhenError() throws Exception {
		testCancelTimeoutOnEvent(backup, backup.getId(), BackupExecutionStatus.ERROR);
	}

	private void testCancelTimeoutOnEvent(Backup backup, String id2, BackupExecutionStatus status) throws
			FullSlotException, BackupNotFoundException {
		scheduler.startTimeoutForBackup(10000, backup.getId(), new SendTimeoutTask(backup.getId(), service));

		boolean hasTimeoutBeforeEvent = scheduler.hasTimeOut(backup.getId());
		service.backupStatus(id2, new Date(), status, "");
		boolean hasTimeoutAfterEvent = scheduler.hasTimeOut(backup.getId());

		assertTrue(hasTimeoutBeforeEvent);
		assertFalse(hasTimeoutAfterEvent);
	}

	@Test
	public void testSkipRescheduleWhenHasNoTimeout() throws Exception {
		backupEntity.setRescheduleTimeout(0L);
		backupEntity.setMaximumReschedules(1);
		backupEntity.setState(BackupState.SCHEDULED);

		testSkipReschedule();
		assertEquals(new Integer(0), backupEntity.getCurrentTries());
	}

	@Test
	public void testSkipRescheduleWhenReachedMaximumTries() throws Exception {
		backupEntity.setRescheduleTimeout(10000L);
		backupEntity.setMaximumReschedules(1);
		backupEntity.increaseTryCount();
		backupEntity.setState(BackupState.SCHEDULED);

		testSkipReschedule();
		assertEquals(new Integer(1), backupEntity.getCurrentTries());
	}

	private void testSkipReschedule() throws BackupNotFoundException {
		service.backupStatus(backup.getId(), new Date(), BackupExecutionStatus.ERROR, "");

		assertEquals(BackupState.ERROR, backupEntity.getState());
		assertFalse(scheduler.hasSchedule(backup.getId()));
	}

	@Test
	public void testRescheduleWhenHasTimeout() throws Exception {
		backupEntity.setRescheduleTimeout(10000L);
		backupEntity.setMaximumReschedules(1);
		backupEntity.setState(BackupState.SCHEDULED);

		service.backupStatus(backup.getId(), new Date(), BackupExecutionStatus.ERROR, "");

		assertEquals(BackupState.SCHEDULED, backupEntity.getState());
		assertEquals(new Integer(1), backupEntity.getCurrentTries());
		assertTrue(scheduler.hasSchedule(backup.getId()));
	}
}