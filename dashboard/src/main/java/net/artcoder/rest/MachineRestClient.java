package net.artcoder.rest;

import java.io.IOException;

public interface MachineRestClient {
	void machineCreate(String ip, String password) throws IOException;

	String machineList() throws IOException;
}
