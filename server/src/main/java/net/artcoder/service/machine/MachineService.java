package net.artcoder.service.machine;

import net.artcoder.dto.NewMachineCommand;

import java.util.List;

public interface MachineService {

	void createMachine(NewMachineCommand dto);

	List<String> findMachines();
}
