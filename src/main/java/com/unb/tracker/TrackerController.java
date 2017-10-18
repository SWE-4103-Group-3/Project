package com.unb.tracker;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@EnableAutoConfiguration
public class TrackerController {
    @RequestMapping(value="/", method = RequestMethod.GET)
    public String index(ModelMap map) {
        return "index";
    }

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
        n.setName(name);
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

    @GetMapping(path="/student")
    public String student(ModelMap map) {
        Iterable<Course> courseList = courseRepository.findAll();
        map.addAttribute("courseList", courseList);
        return "student/student";
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }


    @GetMapping(path="/course")
    public String courseForm(Model model) {
        model.addAttribute("course", new Course());
        return "courseFormView";
    }

    @PostMapping("/course")
    public String courseSave(@ModelAttribute Course course, ModelMap map) {
        courseRepository.save(course);
        map.addAttribute("course", course);
        return "instructor/course";
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
