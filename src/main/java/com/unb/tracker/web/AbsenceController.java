package com.unb.tracker.web;

import com.unb.tracker.model.Absence;
import com.unb.tracker.model.Course;
import com.unb.tracker.model.Seat;
import com.unb.tracker.model.User;
import com.unb.tracker.repository.AbsenceRepository;
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
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class AbsenceController {
    private static final Logger LOG = LoggerFactory.getLogger(AbsenceController.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @PostMapping(value = "/courses/{courseId}/absences")
    public @ResponseBody Course postAbsences(@PathVariable Long courseId, @RequestBody List<User> students) {
        LOG.info("postAbsences - starting");
        Course course = courseRepository.findOne(courseId);

        java.sql.Date today = new java.sql.Date(new java.util.Date().getTime());
        List<Absence> currentAbsences = absenceRepository.findByCourseIdAndDate(courseId, today);
        absenceRepository.delete(currentAbsences);

        for(User s : students) {
            Absence a = new Absence();
            a.setStudent(s);
            a.setDate(today);
            a.setCourse(course);
            absenceRepository.save(a);
        }
        return course;
    }

    @GetMapping(value = "/courses/{courseId}/absences")
    public List<Absence> getAllCourseAbsencesById(@PathVariable Long courseId) {
        LOG.info("getAllCourseAbsences - starting - courseId: {}", courseId);
        return absenceRepository.findByCourseId(courseId);
    }

    @GetMapping(value = "/courses/{courseId}/absences/today")
    public @ResponseBody List<Absence> getAllCourseAbsencesForTodayById(@PathVariable Long courseId) {
        LOG.info("getAllCourseAbsencesForToday - starting - courseId: {}", courseId);
        java.sql.Date today = new java.sql.Date(new java.util.Date().getTime());
        return absenceRepository.findByCourseIdAndDate(courseId, today);
    }

    @GetMapping(value = "/courses/{courseId}/absences/{student}")
    public @ResponseBody List<Absence> getAllStudentAbsencesById(@PathVariable Long courseId, @PathVariable Long studentId) {
        LOG.info("getAllStudentAbsences - starting - courseId: {}, student: {}", courseId, studentId);
        return absenceRepository.findByCourseIdAndStudentId(courseId, studentId);
    }
}
