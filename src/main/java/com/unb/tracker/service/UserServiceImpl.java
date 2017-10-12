package com.unb.tracker.service;

import com.unb.tracker.model.User;
import com.unb.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public void save(User user) {
		user.setUsername(user.getUsername());
		user.setEmail(user.getEmail());
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setExtendedPrivileges(user.hasExtendedPrivileges());
		userRepository.save(user);
	}

	@Override
	public User findByUserName(String username) {
		return userRepository.findByUsername(username);
	}
}