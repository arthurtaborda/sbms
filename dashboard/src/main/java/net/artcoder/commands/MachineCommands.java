package net.artcoder.commands;

import net.artcoder.rest.JsonUtil;
import net.artcoder.rest.MachineRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MachineCommands implements CommandMarker {

	private MachineRestClient machineRestClient;

	@Autowired
	public MachineCommands(MachineRestClient machineRestClient) {
		this.machineRestClient = machineRestClient;
	}


	@CliCommand(value = "machine create", help = "Creates a new client")
	public String machineCreate(
			@CliOption(key = {"ip"}, mandatory = true)
			final String ip,
			@CliOption(key = {"password"}, mandatory = true)
			final String password)
	{
		try {
			machineRestClient.machineCreate(ip, password);

			return "Client created successfully.";
		} catch (IOException e) {
			return "An error occurred: " + e.getMessage();
		}
	}

	@CliCommand(value = "machine list", help = "List all clients")
	public String machineList(
	) {
		try {
			String json = machineRestClient.machineList();

			return JsonUtil.prettify(json);
		} catch (IOException e) {
			return "An error occurred: " + e.getMessage();
		}
	}
}
