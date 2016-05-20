package net.artcoder.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class MachineRestClientImpl extends RestClient implements MachineRestClient {

	@Autowired
	public MachineRestClientImpl(ObjectMapper objectMapper,
								 @Value("${server.address}") String serverAddress) {
		super(objectMapper, serverAddress);
	}

	@Override
	public void machineCreate(String ip, String password) throws IOException {
		ImmutableMap<String, Object> query = ImmutableMap.of("ip", ip, "password", password);
		HTTPResponse<String> response = post("/api/machines", query, null, true);

		genericResponse(response, 201);
	}

	@Override
	public String machineList() throws IOException {
		HTTPResponse<String> response = get("/api/machines", null, null, true);

		return genericReturnResponse(response, 200);
	}
}
