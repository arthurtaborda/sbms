package net.artcoder.persistence.entity;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Entity
@Table(name = "users")
public class UserEntity implements UserDetails {

	@Id
	private String username;
	@NotNull
	private String encryptedPassword;
	private boolean enabled = true;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	private Collection<AuthorityEntity> authorities = new ArrayList<>();

	@Override
	public Collection<AuthorityEntity> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return encryptedPassword;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
