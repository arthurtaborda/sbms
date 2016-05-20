package net.artcoder.config.security;

import net.artcoder.persistence.entity.UserEntity;
import net.artcoder.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserProvider implements org.springframework.security.core.userdetails.UserDetailsService {

	private UserRepository userRepository;

	@Autowired
	public UserProvider(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = userRepository.findOne(username);

		if(user == null) {
			throw new UsernameNotFoundException("User with username " + username + " could not be found");
		}

		return user;
	}
}
