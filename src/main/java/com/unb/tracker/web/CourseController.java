package com.unb.tracker.web;

import com.opencsv.CSVWriter;
import com.unb.tracker.exception.BadRequestException;
import com.unb.tracker.exception.NotFoundException;
import com.unb.tracker.model.Absence;
import com.unb.tracker.model.Course;
import com.unb.tracker.model.Seat;
import com.unb.tracker.model.User;
import com.unb.tracker.repository.AbsenceRepository;
import com.unb.tracker.repository.CourseRepository;
import com.unb.tracker.repository.SeatRepository;
import com.unb.tracker.repository.UserRepository;
import com.unb.tracker.validator.CourseValidator;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private AbsenceRepository absenceRepository;

    @GetMapping(value = "/{username}/{courseName}")
    public String getCourseByName(@PathVariable String username, @PathVariable String courseName, ModelMap map, Principal principal) {
        LOG.info("getCourseByName - starting - username: {}, courseName: {}", username, courseName);

        User user = userRepository.findByUsername(principal.getName());
        map.addAttribute("user", user);

        map.addAttribute("courseList", user.getCourses());

        List<Course> courses = courseRepository.findByInstructorUsernameAndNameAndSection(username, courseName, "");
        LOG.debug("courses size: {}", courses.size());
        if (courses.size() == 0) {
            throw new NotFoundException();
        } else if (courses.size() == 1) {
            map.addAttribute("course", courses.get(0));
            return "course";
        }
        throw new BadRequestException();
    }

    @GetMapping(value = "/{username}/{courseName}/{courseSection}")
    public String getCourseByNameAndSection(@PathVariable String username, @PathVariable String courseName, @PathVariable String courseSection, ModelMap map, Principal principal) {
        LOG.info("getCourseByName - starting - username: {}, courseName: {}; courseSection: {}", username, courseName, courseSection);


        User user = userRepository.findByUsername(principal.getName());
        map.addAttribute("user", user);

        map.addAttribute("courseList", user.getCourses());

        List<Course> courses = courseRepository.findByInstructorUsernameAndNameAndSection(username, courseName, courseSection);
        if (courses.size() == 0) {
            throw new NotFoundException();
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
        for(Seat s : seats) {
            s.removeStudent();
        }
        seatRepository.save(seats);

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
        course.setInstructor(user);

        map.addAttribute("course", course);
        courseValidator.validate(course, bindingResult);
        if (bindingResult.hasErrors()) {
            redir.addFlashAttribute("courseCreationError", bindingResult.getFieldError().getCode());
            redir.addFlashAttribute("courseName", course.getName());
            redir.addFlashAttribute("courseSection", course.getSection());
            redir.addFlashAttribute("startDate", course.getStartDate() == null ? "" : course.getStartDate().toString());
            redir.addFlashAttribute("courseRows", course.getRows());
            redir.addFlashAttribute("courseCols", course.getCols());

            return "redirect:/" + user.getUsername();
        }

        boolean newCourse = false;
        // Are we trying to create a course?
        if (course.getId() == null) {
            newCourse = true;
            course.setInstructor(user);

            Long courseGridReuseID = course.getCourseGridReuseID();
            if(courseGridReuseID != null) {
                Course otherCourse = courseRepository.findOne(courseGridReuseID);
                reuseCourseGridHelper(course, otherCourse);
            }
        } else {
            Course c = courseRepository.findOne(course.getId());
            List<Seat> seatsToRemove = c.getOutOfBoundsSeats(course.getRows(), course.getCols());
            c.removeSeats(seatsToRemove);
            LOG.debug("removing {} seats", seatsToRemove.size());
            seatRepository.save(seatsToRemove);
        }

        if (course.getSection().isEmpty()) {
            course.setSection("");
        }

        courseRepository.save(course);
        
        if(newCourse) {
            LOG.debug("creating empty seats!!");
            List<Seat> seats = new ArrayList<>();
            for(int i = 0; i < course.getRows(); i++) {
                for(int j = 0; j < course.getCols(); j++) {
                    Seat s = new Seat();
                    s.setState(Seat.AVAILABLE);
                    s.setCourse(course);
                    s.setRow(i);
                    s.setCol(j);
                    seats.add(s);
                }
            }
            seatRepository.save(seats);
        }

        map.addAttribute("course", course);

        String redirectUrl = "/" + user.getUsername() + "/" + course.getName();
        if(!course.getSection().equals("")) redirectUrl += "/" + course.getSection();
        return "redirect:" + redirectUrl;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        // true passed to CustomDateEditor constructor means convert empty String to null
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
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

        List<Absence> absences = absenceRepository.findByCourseId(courseId);
        absenceRepository.delete(absences);

        courseRepository.delete(courseId);
        return "success";
    }


    @GetMapping(value = "/courses/{courseId}/export")
    @ResponseBody
    public String exportAttendance(@PathVariable Long courseId, ModelMap map, Principal principal, HttpServletResponse response) {
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer);

        Iterable<Absence> absences = absenceRepository.findByCourseId(courseId);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Course course = courseRepository.findOne(courseId);

        List<String[]> absenceStrings = new ArrayList<>();
        for (Absence absence : absences) {
            absenceStrings.add(new String[]{absence.getStudent().getUsername(), df.format(absence.getDate())});
        }

        String header = "attachment; filename=" + course.getName();
        if(course.getSection() != null && !course.getSection().equals("")) header += "_" + course.getSection();
        header += ".csv";

        response.setHeader("Content-Disposition",  header);
        csvWriter.writeAll(absenceStrings);
        return writer.toString();
    }
}