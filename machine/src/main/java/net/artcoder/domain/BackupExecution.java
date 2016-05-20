package net.artcoder.domain;

import lombok.*;

import java.util.Date;

@Getter
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BackupExecution {

	public enum Status {
		CONNECTING_TO_SOURCE, SENDING_TO_DESTINATION, ERROR, CONNECTING_TO_DESTINATION, DONE
	}

	@Setter
	private String message;
	private Date date = new Date();

	@NonNull
	private Status status;

	public static BackupExecution get(Status status) {
		return new BackupExecution(status);
	}

	public static BackupExecution get(Status status, String message) {
		BackupExecution execution = new BackupExecution(status);
		execution.setMessage(message);
		return execution;
	}
}
