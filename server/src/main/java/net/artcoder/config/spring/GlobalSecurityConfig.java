package net.artcoder.config.spring;

import net.artcoder.persistence.entity.AuthorityEntity;
import net.artcoder.persistence.entity.UserEntity;
import net.artcoder.persistence.repository.AuthorityRepository;
import net.artcoder.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class GlobalSecurityConfig {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private AuthorityRepository authorityRepository;

	@Bean
	public ProviderManager authenticationManager(UserDetailsService userDetailsService) {
		List<AuthenticationProvider> providers = new ArrayList<>();

		DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
		daoProvider.setPasswordEncoder(new BCryptPasswordEncoder());
		daoProvider.setUserDetailsService(userDetailsService);

		providers.add(daoProvider);
		ProviderManager providerManager = new ProviderManager(providers);
		providerManager.setEraseCredentialsAfterAuthentication(true);
		return providerManager;
	}

	@PostConstruct
	public void insertDefaultAdmin() {
		if(!userRepository.exists("admin")) {
			UserEntity user = new UserEntity();
			user.setUsername("admin");
			user.setEncryptedPassword(new BCryptPasswordEncoder().encode("admin"));
			userRepository.save(user);

			authorityRepository.save(new AuthorityEntity("ROLE_ADMIN", user));
		}
	}
}
