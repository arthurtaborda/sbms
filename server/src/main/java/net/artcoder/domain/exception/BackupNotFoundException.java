package net.artcoder.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Backup could not be found")
public class BackupNotFoundException extends Exception {
	public BackupNotFoundException() {
	}

	public BackupNotFoundException(String s) {
		super(s);
	}

	public BackupNotFoundException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public BackupNotFoundException(Throwable throwable) {
		super(throwable);
	}

	public BackupNotFoundException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
