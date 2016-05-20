package net.artcoder.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.Headers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class SecurityRestClientImpl extends RestClient implements SecurityRestClient {

	@Autowired
	public SecurityRestClientImpl(ObjectMapper objectMapper,
								  @Value("${server.address}") String serverAddress) {
		super(objectMapper, serverAddress);
	}

	@Override
	public void login(String username, String password) throws IOException {
		ImmutableMap<String, Object> query = ImmutableMap.of("username", username, "password", password);
		HTTPResponse<String> response = post("/login", query, null, false);

		genericResponse(response, 302);

		Headers headers = response.getHeaders();
		List<String> strings = headers.get("Set-Cookie");

		if (strings != null && !strings.isEmpty()) {
			SessionHolder.setSessionCookie(strings.get(0));
		} else {
			throw new IOException("Wrong credentials");
		}
	}
}
