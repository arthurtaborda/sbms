package net.artcoder.service.machine.impl;

import net.artcoder.dto.NewMachineCommand;
import net.artcoder.persistence.entity.AuthorityEntity;
import net.artcoder.persistence.entity.UserEntity;
import net.artcoder.persistence.repository.AuthorityRepository;
import net.artcoder.persistence.repository.UserRepository;
import net.artcoder.service.machine.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MachineServiceImpl implements MachineService {

	private UserRepository userRepository;
	private AuthorityRepository authorityRepository;

	@Autowired
	public MachineServiceImpl(UserRepository userRepository, AuthorityRepository authorityRepository) {
		this.userRepository = userRepository;
		this.authorityRepository = authorityRepository;
	}

	@Override
	public void createMachine(NewMachineCommand dto) {
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername(dto.getAddress().toString());
		userEntity.setEncryptedPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
		userRepository.save(userEntity);

		authorityRepository.save(new AuthorityEntity("ROLE_MACHINE", userEntity));
	}

	@Override
	public List<String> findMachines() {
		List<String> ips = authorityRepository.findUsernamesFrom("ROLE_MACHINE");

		if (ips == null) {
			return new ArrayList<>();
		}

		return ips;
	}
}
