package net.artcoder.config.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private ProviderManager providerManager;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.csrf().disable().exceptionHandling()
				.and()
				.authorizeRequests().anyRequest().permitAll()
				.and()
				.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.and()
				.logout().logoutUrl("/logout")
				.and()
				.sessionManagement()
				.maximumSessions(-1)
				.sessionRegistry(sessionRegistry());
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public SimpleUrlAuthenticationFailureHandler authFailureHandler() {
		return new SimpleUrlAuthenticationFailureHandler();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.parentAuthenticationManager(providerManager);
	}
}
