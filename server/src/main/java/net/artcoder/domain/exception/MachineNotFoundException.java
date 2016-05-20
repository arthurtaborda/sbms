package net.artcoder.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Client could not be found")
public class MachineNotFoundException extends Exception {
	public MachineNotFoundException() {
	}

	public MachineNotFoundException(String s) {
		super(s);
	}

	public MachineNotFoundException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public MachineNotFoundException(Throwable throwable) {
		super(throwable);
	}

	public MachineNotFoundException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
