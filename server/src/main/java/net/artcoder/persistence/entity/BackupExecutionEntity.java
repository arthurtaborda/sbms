package net.artcoder.persistence.entity;

import lombok.*;
import net.artcoder.domain.BackupExecutionStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Entity
@Table(name = "backup_execution")
public class BackupExecutionEntity {

	@Id
	@NonNull
	private String id;

	@NonNull
	@Enumerated(EnumType.STRING)
	private BackupExecutionStatus status;

	@NonNull
	@NotNull
	@ManyToOne(cascade = CascadeType.REMOVE)
	private BackupEntity backup;

	private String message;

	@NonNull
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateTime;

}
