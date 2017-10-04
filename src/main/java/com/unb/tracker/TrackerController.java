package com.unb.tracker;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@EnableAutoConfiguration
public class TrackerController {
    @RequestMapping(value="/", method = RequestMethod.GET)
    public String index(ModelMap map) {
        return "index";
    }

    @Autowired
    private UserRepository userRepository;
    @RequestMapping(value="/add", method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})

    public @ResponseBody
    String addNewUser (@RequestParam String name
            , @RequestParam String email) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request


        User n = new User();
        n.setName(name);
        n.setEmail(email);
        userRepository.save(n);
        return "Saved";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }

    @GetMapping(path="/course/form")
    public String courseForm(Model model) {
        model.addAttribute("course", new Course());
        return "courseFormView";
    }
    @Autowired
    private CourseRepository courseRepository;
    @PostMapping("/course/save")
    public String courseSave(@ModelAttribute Course course) {
        courseRepository.save(course);
        return "courseFormView";
    }


}
