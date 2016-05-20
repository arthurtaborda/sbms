package net.artcoder.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class RestClient {

	protected ObjectMapper objectMapper;
	private String serverAddress;

	public RestClient(ObjectMapper objectMapper, String serverAddress) {
		this.objectMapper = objectMapper;
		this.serverAddress = serverAddress;
	}

	protected HTTPResponse<String> get(String uri, Map<String, Object> queryStrings, Map<String, String>
			routeParams, boolean authenticate) throws IOException {
		return genericRequest(routeParams, authenticate, Unirest.get(serverAddress + uri).queryString(queryStrings));
	}

	protected HTTPResponse<String> post(String uri, Map<String, Object> queryStrings, Map<String, String>
			routeParams, boolean authenticate) throws IOException {
		return genericRequest(routeParams, authenticate, Unirest.post(serverAddress + uri).queryString(queryStrings));
	}

	protected HTTPResponse<String> delete(String uri, Map<String, Object> queryStrings, Map<String, String>
			routeParams, boolean authenticate) throws IOException {
		return genericRequest(routeParams, authenticate, Unirest.delete(serverAddress + uri).queryString(queryStrings));
	}

	private HTTPResponse<String> genericRequest(Map<String, String> routeParams,
												boolean authenticate, HttpRequest httpRequestWithBody) throws IOException {
		if (routeParams != null) {
			for (String s : routeParams.keySet()) {
				httpRequestWithBody.routeParam(s, routeParams.get(s));
			}
		}

		if (authenticate) {
			httpRequestWithBody.header("Cookie", SessionHolder.getSessionCookie());
		}

		try {
			return new HTTPResponse<>(httpRequestWithBody.asString());
		} catch (UnirestException e) {
			throw new IOException(e);
		}
	}

	protected HTTPResponse<String> createBackupRequest(String ip, String source, String destination,
													   String sourceDomain, String sourceUser, String sourcePass,
													   String destinationDomain, String destinationUser,
													   String destinationPass, Long rescheduleTimeout,
													   Integer maximumReschedules, String datetime) throws IOException {

		Map<String, Object> query = new HashMap<>();
		query.put("ip", ip);
		query.put("source", source);
		query.put("destination", destination);
		query.put("sourceDomain", sourceDomain);
		query.put("destinationDomain", destinationDomain);

		if (!StringUtils.isEmpty(sourceUser)) {
			query.put("sourceUser", sourceUser);
			query.put("sourcePass", sourcePass);
		}

		if (!StringUtils.isEmpty(destinationUser)) {
			query.put("destinationUser", destinationUser);
			query.put("destinationPass", destinationPass);
		}

		if (rescheduleTimeout != null && rescheduleTimeout > 0) {
			query.put("rescheduleTimeout", rescheduleTimeout);
			query.put("maximumReschedules", maximumReschedules);
		}

		if (datetime != null) {
			query.put("datetime", datetime);
		}

		return post("/api/backups", query, null, true);
	}

	protected String genericReturnResponse(HTTPResponse<String> response, Integer expectedStatus,
										   Integer... expectedStatusList) throws IOException {

		genericResponse(response, expectedStatus, expectedStatusList);
		return response.getBody();
	}

	protected void genericResponse(HTTPResponse<String> response, Integer expectedStatus,
								   Integer... expectedStatusList) throws IOException {

		if (response.getStatus() == expectedStatus ||
				Arrays.asList(expectedStatusList).contains(response.getStatus())) {
			return;
		} else if (response.isClientError() || response.isServerError()) {
			Error error = objectMapper.readValue(response.getBody(), Error.class);
			throw new RequestException(error.toString());
		}

		throw new IOException("Return code " + response.getStatus() + " not expected");
	}
}
