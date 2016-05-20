package net.artcoder.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.artcoder.domain.exception.AlreadyActiveException;
import net.artcoder.domain.exception.AlreadyDisabledException;
import net.artcoder.service.backup.task.AddBackupToQueue;

import java.util.Date;
import java.util.concurrent.Future;

@AllArgsConstructor
public class BackupSchedule {

	private Future future;
	@Getter
	private AddBackupToQueue task;
	@Getter
	private Date date;

	public boolean cancel() throws AlreadyDisabledException {
		boolean cancelled = false;
		if (!future.isCancelled()) {
			cancelled = future.cancel(false);
		} else {
			throw new AlreadyDisabledException("Schedule should be active to be disabled");
		}
		return cancelled;
	}

	public void setFuture(Future newFuture) throws AlreadyActiveException {
		if (!isActive()) {
			this.future = newFuture;
		} else {
			newFuture.cancel(true);
			throw new AlreadyActiveException("Schedule should be done or cancelled to be replaced");
		}
	}

	public boolean isActive() {
		return !future.isCancelled() && !task.isExecuted();
	}
}
