package net.artcoder.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.artcoder.domain.Backup;
import net.artcoder.domain.IP;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class NewBackupCommand {

	@NonNull
	private IP address;

	@Setter
	private LocalDateTime dateAndTime;

	@NonNull
	private Backup backup;
}
