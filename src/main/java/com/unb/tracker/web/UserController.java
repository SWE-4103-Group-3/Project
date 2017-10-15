package com.unb.tracker.web;

import com.unb.tracker.model.User;
import com.unb.tracker.service.SecurityService;
import com.unb.tracker.service.UserService;
import com.unb.tracker.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserValidator userValidator;

	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@RequestMapping(value = {"/"}, method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("sign-in-form", new User());
		model.addAttribute("sign-up-form", new User());
		return "index";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String registration(@ModelAttribute("sign-up-form") User userForm, BindingResult bindingResult, Model model) {
		LOG.info("User " + userForm.getUsername() + " priviledges: " + userForm.getHasExtendedPrivileges());
		userValidator.validate(userForm, bindingResult);

		if (bindingResult.hasErrors())
		{
			model.addAttribute("accountCreationError", "Something went wrong.");
			return "index";
		}

		userService.save(userForm);
		securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

		return "redirect:/";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, String loginError, String logout) {
		if (loginError != null)
			model.addAttribute("loginError", "Invalid username or password.");

		return "index";
	}
}