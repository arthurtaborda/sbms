package net.artcoder.rest;

import com.mashape.unirest.http.Headers;
import com.mashape.unirest.http.HttpResponse;

import java.io.InputStream;

public class HTTPResponse<T> {

	private com.mashape.unirest.http.HttpResponse<T> response;

	public HTTPResponse(HttpResponse<T> response) {
		this.response = response;
	}

	public int getStatus() {
		return response.getStatus();
	}

	public InputStream getRawBody() {
		return response.getRawBody();
	}

	public T getBody() {
		return response.getBody();
	}

	public Headers getHeaders() {
		return response.getHeaders();
	}

	public String getStatusText() {
		return response.getStatusText();
	}

	public boolean isInformation() {
		return getStatus() >= 100 && getStatus() < 200;
	}

	public boolean isSuccess() {
		return getStatus() >= 200 && getStatus() < 300;
	}

	public boolean isRedirection() {
		return getStatus() >= 300 && getStatus() < 400;
	}

	public boolean isClientError() {
		return getStatus() >= 400 && getStatus() < 500;
	}

	public boolean isServerError() {
		return getStatus() >= 500 && getStatus() < 600;
	}
}
