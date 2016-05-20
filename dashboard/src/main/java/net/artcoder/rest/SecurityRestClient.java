package net.artcoder.rest;

import java.io.IOException;

public interface SecurityRestClient {
	void login(String username, String password) throws IOException;
}
