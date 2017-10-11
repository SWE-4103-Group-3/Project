package com.unb.tracker.service;

import com.unb.tracker.model.User;

public interface UserService {
	void save(User user);

	User findByUserName(String username);
}