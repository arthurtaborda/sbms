package net.artcoder.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.LOCKED, reason = "Full slot")
public class FullSlotException extends Exception {
	public FullSlotException() {
	}

	public FullSlotException(String s) {
		super(s);
	}

	public FullSlotException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public FullSlotException(Throwable throwable) {
		super(throwable);
	}

	public FullSlotException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
