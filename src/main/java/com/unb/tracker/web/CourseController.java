package com.unb.tracker.web;

import com.unb.tracker.model.Course;
import com.unb.tracker.model.Seat;
import com.unb.tracker.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class CourseController {
    private static final Logger LOG = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping(value="/{username}/{courseName}")
    public String getCourseByName(@PathVariable String username, @PathVariable String courseName, ModelMap map) {
        LOG.info("getCourseByName - starting - username: {}, courseName: {}", username, courseName);
        List<Course> courses = courseRepository.findByInstructorUsernameAndName(username, courseName);
        LOG.debug("courses size: {}", courses.size());
        if (courses.size() == 0){
            return "404";
        } else if(courses.size() == 1) {
            map.addAttribute("course", courses.get(0));
            return "instructor/course";
        }
        return "error";
    }

    @GetMapping(value="/{username}/{courseName}/{courseSection}")
    public String getCourseByNameAndSection(@PathVariable String courseName, @PathVariable String courseSection, ModelMap map) {
        List<Course> courses = courseRepository.findByNameAndSection(courseName, courseSection);
        if (courses.size() == 0) {
            return "404";
        } else if(courses.size() == 1) {
            map.addAttribute("course", courses.get(0));
            return "instructor/course";
        }
        return "error";
    }

    @GetMapping(value="/courses/{courseId}")
    public @ResponseBody
    Course getCourse(@PathVariable Long courseId) {
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
