package net.artcoder.commands;

import net.artcoder.rest.SecurityRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityCommands implements CommandMarker {

	private SecurityRestClient securityRestClient;

	@Autowired
	public SecurityCommands(SecurityRestClient securityRestClient) {
		this.securityRestClient = securityRestClient;
	}



	@CliCommand(value = "login", help = "Logs in as admin")
	public String login(
			@CliOption(key = {"u", "username"}, mandatory = true)
			final String username,
			@CliOption(key = {"p", "password"}, mandatory = true)
			final String password
	) {
		try {
			securityRestClient.login(username, password);
			return "Login successful";
		} catch (IOException e) {
			return "An error occurred: " + e.getMessage();
		}
	}
}
