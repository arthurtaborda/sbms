package net.artcoder.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.artcoder.domain.IP;

@Getter
@RequiredArgsConstructor
public class NewMachineCommand {

	@NonNull
	private IP address;

	@NonNull
	private String password;
}
