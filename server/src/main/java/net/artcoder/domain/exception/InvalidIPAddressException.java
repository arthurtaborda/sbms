package net.artcoder.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "IP address is not valid")
public class InvalidIPAddressException extends RuntimeException {
	public InvalidIPAddressException() {
	}

	public InvalidIPAddressException(String s) {
		super(s);
	}

	public InvalidIPAddressException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public InvalidIPAddressException(Throwable throwable) {
		super(throwable);
	}

	public InvalidIPAddressException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
