package com.unb.tracker.web;

import com.unb.tracker.exception.BadRequestException;
import com.unb.tracker.exception.NotFoundException;
import com.unb.tracker.model.Course;
import com.unb.tracker.model.Seat;
import com.unb.tracker.model.User;
import com.unb.tracker.repository.CourseRepository;
import com.unb.tracker.repository.SeatRepository;
import com.unb.tracker.repository.UserRepository;
import com.unb.tracker.validator.CourseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@EnableAutoConfiguration
public class CourseController {
    private static final Logger LOG = LoggerFactory.getLogger(CourseController.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseValidator courseValidator;

    @Autowired
    private SeatRepository seatRepository;

    @GetMapping(value = "/{username}/{courseName}")
    public String getCourseByName(@PathVariable String username, @PathVariable String courseName, ModelMap map, Principal principal) {
        LOG.info("getCourseByName - starting - username: {}, courseName: {}", username, courseName);

        User user = userRepository.findByUsername(principal.getName());
        map.addAttribute("user", user);

        map.addAttribute("courseList", user.getCourses());

        List<Course> courses = courseRepository.findByInstructorUsernameAndName(username, courseName);
        LOG.debug("courses size: {}", courses.size());
        if (courses.size() == 0) {
            return "404";
        } else if (courses.size() == 1) {
            map.addAttribute("course", courses.get(0));
            return "course";
        }
        return "error";
    }

    @GetMapping(value = "/{username}/{courseName}/{courseSection}")
    public String getCourseByNameAndSection(@PathVariable String username, @PathVariable String courseName, @PathVariable String courseSection, ModelMap map, Principal principal) {
        LOG.info("getCourseByName - starting - username: {}, courseName: {}; courseSection: {}", username, courseName, courseSection);


        User user = userRepository.findByUsername(principal.getName());
        map.addAttribute("user", user);

        map.addAttribute("courseList", user.getCourses());

        List<Course> courses = courseRepository.findByInstructorUsernameAndNameAndSection(username, courseName, courseSection);
        if (courses.size() == 0) {
            return "404";
        } else if (courses.size() == 1) {
            map.addAttribute("course", courses.get(0));
            return "course";

        }
        throw new BadRequestException();
    }

    @GetMapping(value = "/courses/{courseId}")
    public @ResponseBody
    Course getCourse(@PathVariable Long courseId) {
        LOG.info("getCourse - starting - courseId: {}", courseId);
        Course course = courseRepository.findOne(courseId);
        if (course == null) {
            throw new NotFoundException();
        }
        return course;
    }

    @PostMapping(value = "/courses/{courseId}/seats")
    public @ResponseBody
    Course postCourseSeats(@PathVariable Long courseId, @RequestBody List<Seat> seats) {
        LOG.info("postCourseSeats - starting");
        Course course = courseRepository.findOne(courseId);

        for(Seat s : seats) {
            s.setCourse(course);
        }
        course.setSeats(seats);

        seatRepository.save(seats);
        courseRepository.save(course);

        return course;
    }

    @PostMapping(value = "/courses/{courseId}/seats/students/remove")
    public @ResponseBody String removeStudentsFromSeats(@PathVariable Long courseId) {
        LOG.info("removeStudentsFromSeats - starting");
        Course course = courseRepository.findOne(courseId);
        List<Seat> seats = course.getSeats();

        List<User> students = new ArrayList<>();
        for(Seat s : seats) {
            User student = s.getStudent();
            if(student != null) {
                student.removeSeat(s);
                students.add(student);
                s.removeStudent();
            }
        }

        seatRepository.save(seats);
        userRepository.save(students);

        return "saved";
    }

    @GetMapping(value = "/courses")
    public @ResponseBody
    Iterable<Course> getCourses() {
        return courseRepository.findAll();
    }

    @PostMapping("/course")
    public String courseSave(@ModelAttribute Course course, ModelMap map, Principal principal, BindingResult bindingResult, RedirectAttributes redir) {
        LOG.info("courseSave - starting - principle.name: {}", principal.getName());
        User user = userRepository.findByUsername(principal.getName());
        if (user == null || !user.getHasExtendedPrivileges()) {
            throw new BadRequestException();
        }

        map.addAttribute("course", course);
        courseValidator.validate(course, bindingResult);
        if (bindingResult.hasErrors()) {
            redir.addFlashAttribute("courseCreationError", bindingResult.getFieldError().getCode());
            redir.addFlashAttribute("courseName", course.getName());
            redir.addFlashAttribute("sectionName", course.getSection());
            redir.addFlashAttribute("startDate", course.getStartDate().toString());
            redir.addFlashAttribute("rowsAmount", course.getRows());
            redir.addFlashAttribute("colsAmount", course.getCols());
        }

        // Are we trying to create a course?
        if (course.getId() == null) {
            course.setInstructor(user);
            if(bindingResult.hasErrors()) {
                return "redirect:/" + user.getUsername();
            } else {
                Long courseGridReuseID = course.getCourseGridReuseID();
                if(courseGridReuseID != null) {
                    Course otherCourse = courseRepository.findOne(courseGridReuseID);
                    reuseCourseGridHelper(course, otherCourse);
                }
            }

        } else {
            Course c = courseRepository.findOne(course.getId());
            List<Seat> seatsToRemove = c.getOutOfBoundsSeats(course.getRows(), course.getCols());
            c.removeSeats(seatsToRemove);
            LOG.debug("removing {} seats", seatsToRemove.size());
            seatRepository.save(seatsToRemove);
        }

        if(!bindingResult.hasErrors()) {
            courseRepository.save(course);
        }

        return "redirect:/" + user.getUsername() + "/" + course.getName() + "/" + course.getSection();
    }

    @GetMapping("/courses/query/{queryString}")
    public @ResponseBody List<Course> queryCourses(@PathVariable String queryString) {
        return courseRepository.findByPartialName(queryString);
    }

    @PostMapping("/course/gridReuse")
    public @ResponseBody Course reuseCourseGrid(@RequestBody Map<String, Long> courseIds, Principal principal) throws BadRequestException {
        User user = userRepository.findByUsername(principal.getName());
        if (user == null || !user.getHasExtendedPrivileges()) {
            throw new BadRequestException();
        }

        Long courseReceiveID = courseIds.get("currentCourse");
        Long courseGiveID = courseIds.get("otherCourse");

        if(courseReceiveID == null || courseGiveID == null || courseReceiveID.equals(courseGiveID)) {
            throw new BadRequestException();
        }

        Course courseReceive = courseRepository.findOne(courseReceiveID);
        Course courseGive = courseRepository.findOne(courseGiveID);

        reuseCourseGridHelper(courseReceive, courseGive);
        LOG.debug("courseReceive number of seats: {}", courseReceive.getSeats().size());

        courseRepository.save(courseReceive);

        LOG.debug("courseReceive number of seats from db: {}", courseRepository.findOne(courseReceiveID).getSeats().size());

        return courseReceive;
    }

    private void reuseCourseGridHelper(Course courseReceive, Course courseGive) throws BadRequestException {
        if(courseReceive == null || courseGive == null) {
            throw new BadRequestException();
        }

        List<Seat> otherCourseSeats = courseGive.getSeats();
        List<Seat> newCourseSeats = new ArrayList<>();

        courseReceive.setRows(courseGive.getRows());
        courseReceive.setCols(courseGive.getCols());

        //Create a duplicate of each seat found in the original course grid
        for(Seat seat : otherCourseSeats)
        {
            Seat newSeat = new Seat();

            newSeat.setRow(seat.getRow());
            newSeat.setCol(seat.getCol());
            newSeat.setState(seat.getState());
            newSeat.setCourse(courseReceive);

            newCourseSeats.add(newSeat);
        }

        courseReceive.setSeats(newCourseSeats);
    }


    @PostMapping(value = "courses/{courseId}/delete")
    @ResponseBody
    public String deleteCourse(@PathVariable Long courseId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName());
        if (user == null || !user.getHasExtendedPrivileges()) {
            throw new BadRequestException();
        }
        courseRepository.delete(courseId);
        return "success";
    }

}
