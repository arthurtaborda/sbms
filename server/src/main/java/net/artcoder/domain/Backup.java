package net.artcoder.domain;

import lombok.*;
import org.springframework.util.Assert;

@Data
@EqualsAndHashCode(of = "id")
public class Backup {

	@NonNull
	private String id;

	private BackupState state = BackupState.SCHEDULED;

	@NonNull
	private String source;
	@NonNull
	private String sourceDomain;
	@Setter(AccessLevel.NONE)
	private String sourceUser;
	@Setter(AccessLevel.NONE)
	private String sourcePass;

	@NonNull
	private String destination;
	@NonNull
	private String destinationDomain;
	@Setter(AccessLevel.NONE)
	private String destinationUser;
	@Setter(AccessLevel.NONE)
	private String destinationPass;

	private int currentTries;
	private Long rescheduleTimeout;
	private Integer maximumReschedules;

	public void setTimeout(long rescheduleTimeout, int maximumReschedules) {
		Assert.isTrue(rescheduleTimeout > 1000, "Timeout must be higher than 1 sec");
		Assert.isTrue(maximumReschedules < 100, "Maximum tries must be lower than 100");
		Assert.isTrue(maximumReschedules > 0, "Maximum tries must be higher than 0");

		this.maximumReschedules = maximumReschedules;
		this.rescheduleTimeout = rescheduleTimeout;
	}

	public void clearSourceCredentials() {
		this.sourceUser = null;
		this.sourcePass = null;
	}

	public void clearDestinationCredentials() {
		this.destinationUser = null;
		this.destinationPass = null;
	}

	public void setSourceAccessCredentials(String user, String pass) {
		Assert.hasText(user, "User must not be null");
		Assert.hasText(pass, "Pass must not be null");

		this.sourceUser = user;
		this.sourcePass = pass;
	}

	public void setDestinationAccessCredentials(String user, String pass) {
		Assert.hasText(user, "User must not be null");
		Assert.hasText(pass, "Pass must not be null");

		this.destinationUser = user;
		this.destinationPass = pass;
	}
}
