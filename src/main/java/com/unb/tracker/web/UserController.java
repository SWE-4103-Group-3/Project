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
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Controller
public class UserController {
	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserValidator userValidator;

	@RequestMapping(value = {"/"}, method = RequestMethod.GET)
	public String index(Model model, @RequestParam(value = "error", required = false) String error) {
		LOG.info("index - starting - error: {}", error);
		model.addAttribute("sign-in-form", new User());
		model.addAttribute("sign-up-form", new User());
		if(error != null) {
			model.addAttribute("loginError", "Invalid username or password");
		}
		return "index";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String registration(@ModelAttribute("sign-up-form") User userForm, BindingResult bindingResult, Model model) {
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

	@GetMapping(value = "/login/success")
	public String loginSucess(Principal principal) {
		LOG.info("loginSucess - starting");
		return "redirect:/" + principal.getName();
	}
}