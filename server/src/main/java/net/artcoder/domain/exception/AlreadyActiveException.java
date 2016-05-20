package net.artcoder.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "This backup is already enabled")
public class AlreadyActiveException extends Exception {
	public AlreadyActiveException() {
	}

	public AlreadyActiveException(String s) {
		super(s);
	}

	public AlreadyActiveException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public AlreadyActiveException(Throwable throwable) {
		super(throwable);
	}

	public AlreadyActiveException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
