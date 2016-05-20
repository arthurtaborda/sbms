package net.artcoder.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import net.artcoder.dto.ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class BackupRestClientImpl extends RestClient implements BackupRestClient {

	@Autowired
	public BackupRestClientImpl(ObjectMapper objectMapper,
								@Value("${server.address}") String serverAddress) {
		super(objectMapper, serverAddress);
	}

	@Override
	public String backupCreate(String ip, String source, String destination, String sourceDomain,
							   String sourceUser, String sourcePass, String destinationDomain,
							   String destinationUser, String destinationPass, Long rescheduleTimeout,
							   Integer maximumReschedules, String datetime) throws IOException {

		HTTPResponse<String> response = createBackupRequest(ip, source, destination,
				sourceDomain, sourceUser, sourcePass, destinationDomain, destinationUser, destinationPass,
				rescheduleTimeout, maximumReschedules, datetime);

		String body = genericReturnResponse(response, 201);
		ID id = objectMapper.readValue(body, ID.class);
		return id.value;
	}

	@Override
	public void backupEnable(String backupId) throws IOException {
		ImmutableMap<String, String> route = ImmutableMap.of("backupId", backupId);
		HTTPResponse<String> response = post("/api/backups/{backupId}/enable", null, route, true);

		genericResponse(response, 200);
	}

	@Override
	public void backupDisable(String backupId) throws IOException {
		ImmutableMap<String, String> route = ImmutableMap.of("backupId", backupId);
		HTTPResponse<String> response = post("/api/backups/{backupId}/disable", null, route, true);

		genericResponse(response, 200);
	}

	@Override
	public void backupDelete(String backupId) throws IOException {
		ImmutableMap<String, String> route = ImmutableMap.of("backupId", backupId);
		HTTPResponse<String> response = delete("/api/backups/{backupId}/delete", null, route, true);

		genericResponse(response, 200);
	}

	@Override
	public String getBackupDetails(String backupId) throws IOException {
		ImmutableMap<String, String> route = ImmutableMap.of("backupId", backupId);
		HTTPResponse<String> response = get("/api/backups/{backupId}", null, route, true);

		return genericReturnResponse(response, 200);
	}

	@Override
	public String getBackupsFromMachine(String ip) throws IOException {
		ImmutableMap<String, String> route = ImmutableMap.of("ip", ip);
		HTTPResponse<String> response = get("/api/machines/{ip}/backups", null, route, true);

		return genericReturnResponse(response, 200);
	}
}
