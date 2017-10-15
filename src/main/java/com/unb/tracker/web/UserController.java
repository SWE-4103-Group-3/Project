package com.unb.tracker.web;

import com.unb.tracker.model.Course;
import com.unb.tracker.model.User;
import com.unb.tracker.repository.CourseRepository;
import com.unb.tracker.repository.UserRepository;
import com.unb.tracker.service.SecurityService;
import com.unb.tracker.service.UserService;
import com.unb.tracker.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@EnableAutoConfiguration
public class UserController {
	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CourseRepository courseRepository;

	@RequestMapping(value = {"/", "/signup"}, method = RequestMethod.GET)
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
			model.addAttribute("accountCreationError", bindingResult.getFieldError().getCode());
			model.addAttribute("accountType", userForm.getHasExtendedPrivileges() ? "Instructor" : "Student");
			return "index";
		}

		userService.save(userForm);
		securityService.autologin(userForm.getUsername(), userForm.getPasswordConfirm());

		return "redirect:/" + userForm.getUsername();
	}

	@GetMapping(value = "/login/success")
	public String loginSucess(Principal principal) {
		LOG.info("loginSucess - starting");
		return "redirect:/" + principal.getName();
	}

	@GetMapping(path="/{username}")
	public String dashboard(ModelMap map, Principal principal, @PathVariable String username) {
		LOG.info("dashboard - starting - username: {}; principle.name: {}", username, principal.getName());
		if(!principal.getName().equals(username)) {
			return "404";
		}
		User user = userRepository.findByUsername(username);
		map.addAttribute("user", user);
		Iterable<Course> courseList = courseRepository.findAll();
		map.addAttribute("courseList", courseList);
		return "dashboard";
	}
}