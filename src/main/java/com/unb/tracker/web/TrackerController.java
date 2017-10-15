package com.unb.tracker.web;


import com.unb.tracker.TrackerApplication;
import com.unb.tracker.model.Course;
import com.unb.tracker.model.Seat;
import com.unb.tracker.model.User;
import com.unb.tracker.repository.CourseRepository;
import com.unb.tracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class TrackerController {
    private static final Logger LOG = LoggerFactory.getLogger(TrackerController.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value="/add-course", method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    public @ResponseBody String addNewCourse (@RequestParam String name, @RequestParam int rows, @RequestParam int cols) {
        Course c = new Course();
        c.setName(name);
        c.setCols(cols);
        c.setRows(rows);
        courseRepository.save(c);
        return "Saved";
    }

    @RequestMapping(value="/add-user", method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    public @ResponseBody String addNewUser (@RequestParam String name, @RequestParam String email) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request


        User n = new User();
        n.setUsername(name);
        n.setEmail(email);
        userRepository.save(n);
        return "Saved";
    }

    @GetMapping(path="/instructor")
    public String instructor(ModelMap map) {
        Iterable<Course> courseList = courseRepository.findAll();
        map.addAttribute("courseList", courseList);
        return "instructor/instructor";
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

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }

    @GetMapping(value="/instructor/{courseName}/{courseSection}") // TODO: Course should also be identified by instructor name
    public String getCourseByNameAndSection(@PathVariable String courseName, @PathVariable String courseSection, ModelMap map) {
        List<Course> courses = courseRepository.findByNameAndSection(courseName, courseSection);
        if(courses.size() == 1) {
            Iterable<Course> courseList = courseRepository.findAll();
            map.addAttribute("courseList", courseList);
            map.addAttribute("course", courses.get(0));
            return "instructor/course";
        }
        return "404";
    }

    @GetMapping(value="/instructor/{courseName}") // TODO: Course should also be identified by instructor name
    public String getCourseByName(@PathVariable String courseName, ModelMap map) {
        List<Course> courses = courseRepository.findByName(courseName);
        if(courses.size() == 1) {
            Iterable<Course> courseList = courseRepository.findAll();
            map.addAttribute("courseList", courseList);
            map.addAttribute("course", courses.get(0));
            return "instructor/course";
        }
        return "404";
    }

    @GetMapping(value="/courses/{courseId}")
    public @ResponseBody Course getCourse(@PathVariable Long courseId) {
        return courseRepository.findOne(courseId);
    }

    @PostMapping(value="/courses/{courseId}/seats")
    public @ResponseBody Course postCourseSeats(@PathVariable Long courseId, @RequestBody List<Seat> seats) {
        Course course = courseRepository.findOne(courseId);
        course.setSeats(seats);
        courseRepository.save(course);
        return course;
    }

    @PostMapping(value="/courses")
    @ResponseBody
    public String postCourse(@RequestBody Course course) {
        courseRepository.save(course);
        return "Saved";
    }

    @GetMapping(value="/courses")
    public @ResponseBody Iterable<Course> getCourses() {
        return courseRepository.findAll();
    }

}
