package net.artcoder.rest;

import java.io.IOException;

public class RequestException extends IOException {
	public RequestException() {
	}

	public RequestException(String s) {
		super(s);
	}

	public RequestException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public RequestException(Throwable throwable) {
		super(throwable);
	}
}
