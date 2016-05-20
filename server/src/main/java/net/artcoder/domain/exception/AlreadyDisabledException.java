package net.artcoder.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Backup is already disabled")
public class AlreadyDisabledException extends Exception {
	public AlreadyDisabledException() {
	}

	public AlreadyDisabledException(String s) {
		super(s);
	}

	public AlreadyDisabledException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public AlreadyDisabledException(Throwable throwable) {
		super(throwable);
	}

	public AlreadyDisabledException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
