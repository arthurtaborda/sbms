package net.artcoder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.Headers;
import lombok.extern.slf4j.Slf4j;
import net.artcoder.domain.Backup;
import net.artcoder.domain.BackupExecution;
import net.artcoder.domain.BackupFileFactory;
import net.artcoder.domain.Error;
import net.artcoder.rest.HTTPResponse;
import net.artcoder.rest.RestClient;
import net.artcoder.rest.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
@Component
public class ServerServiceImpl extends RestClient implements ServerService {

	private String username;
	private String password;
	private BackupFileFactory backupFileFactory;

	@Autowired
	public ServerServiceImpl(ObjectMapper objectMapper,
							 @Value("${server.address}") String serverAddress,
							 @Value("${server.username}") String username,
							 @Value("${server.password}") String password,
							 BackupFileFactory backupFileFactory) {
		super(objectMapper, serverAddress);
		this.username = username;
		this.password = password;
		this.backupFileFactory = backupFileFactory;
	}

	@Override
	public Backup getBackup() {
		try {
			if (SessionHolder.isClean())
				login();

			ImmutableMap<String, Object> query = ImmutableMap.of("ip", username);
			HTTPResponse<String> response = get("/api/backups", query, null, true);

			int status = response.getStatus();
			boolean forbidden = status == 403;
			boolean ok = status == 200;
			boolean noContent = status == 204;

			if (forbidden) {
				login();
			} else if (ok) {
				Backup backup = new Backup(backupFileFactory);
				return objectMapper.readerForUpdating(backup).readValue(response.getBody());
			} else if (response.isClientError() || response.isServerError()) {
				Error error = objectMapper.readValue(response.getBody(), Error.class);
				System.out.println(error);
			} else if (noContent) {
				System.out.println("No content");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}

		return null;
	}

	private void login() throws IOException {
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

	@Override
	public void sendStatusToServer(BackupExecution backupStatus, String backupId) throws IOException {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		df.setTimeZone(tz);
		String nowAsISO = df.format(backupStatus.getDate());


		Map<String, Object> query = new HashMap<>();
		query.put("status", backupStatus.getStatus().toString());
		if (backupStatus.getMessage() != null) {
			query.put("message", backupStatus.getMessage());
		}
		query.put("datetime", nowAsISO);
		ImmutableMap<String, String> route = ImmutableMap.of("backupId", backupId);

		HTTPResponse<String> response = post("/api/backups/{backupId}/status", query, route, true);

		genericResponse(response, 200);
	}
}
