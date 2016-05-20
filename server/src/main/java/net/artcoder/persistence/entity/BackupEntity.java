package net.artcoder.persistence.entity;

import lombok.*;
import net.artcoder.domain.Backup;
import net.artcoder.domain.BackupState;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "backup")
public class BackupEntity {

	@Id
	@NonNull
	private String id;

	@Enumerated(EnumType.STRING)
	private BackupState state;

	@NonNull
	@NotNull
	private String source;
	@NonNull
	@NotNull
	private String sourceDomain;
	private String sourceUser;
	private String sourcePass;

	@NonNull
	@NotNull
	private String destination;
	@NonNull
	@NotNull
	private String destinationDomain;
	private String destinationUser;
	private String destinationPass;

	@NonNull
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateTime;

	private Long rescheduleTimeout = 0L;
	private Integer maximumReschedules = 0;
	@Setter(AccessLevel.NONE)
	private Integer currentTries = 0;

	@NonNull
	@NotNull
	private String ip;

	public void increaseTryCount() {
		currentTries++;
	}

	public Backup getBackup() {
		Backup backup = new Backup(this.getId(), this.getSource(),
				this.getSourceDomain(), this.getDestination(), this.getDestinationDomain());

		String sourceUser = this.getSourceUser();
		String sourcePass = this.getSourcePass();
		if (StringUtils.hasText(sourceUser) && StringUtils.hasText(sourcePass)) {
			backup.setSourceAccessCredentials(sourceUser, sourcePass);
		}

		String destinationUser = this.getDestinationUser();
		String destinationPass = this.getDestinationPass();
		if (StringUtils.hasText(destinationUser) && StringUtils.hasText(destinationPass)) {
			backup.setDestinationAccessCredentials(destinationUser, destinationPass);
		}

		backup.setRescheduleTimeout(this.rescheduleTimeout);
		backup.setMaximumReschedules(this.maximumReschedules);
		backup.setCurrentTries(this.currentTries);

		backup.setState(this.getState());
		return backup;
	}

}
