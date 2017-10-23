package com.unb.tracker.validator;

import com.unb.tracker.model.User;
import com.unb.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
	@Autowired
	private UserService userService;

	@Override
	public boolean supports(Class<?> aClass) {
		return User.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		User user = (User) o;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "Email field cannot be empty.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "Username field cannot be empty.");
		if (user.getUsername().length() <= 6 || user.getUsername().length() >= 32)
			errors.rejectValue("username", "Username must be between 6 and 32 characters.");

		if (userService.findByUsername(user.getUsername()) != null)
			errors.rejectValue("username", "Someone already has that username.");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "Password field cannot be empty.");
		if (user.getPassword().length() <= 8 || user.getPassword().length() >= 32)
			errors.rejectValue("password", "Password must be between 8 and 32 characters in length.");

		if (!user.getPasswordConfirm().equals(user.getPassword()))
			errors.rejectValue("passwordConfirm", "Your passwords don't match.");
	}
}
