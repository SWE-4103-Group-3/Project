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
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public class AbsenceController {
    private static final Logger LOG = LoggerFactory.getLogger(AbsenceController.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @PostMapping(value = "/courses/{courseId}/absences")
    public @ResponseBody
    
    Course postAbsence(@PathVariable Long courseId, @RequestBody List<User> students) {
        LOG.info("postAbsence - starting");
        Course course = courseRepository.findOne(courseId);

        java.sql.Date today = new java.sql.Date(new java.util.Date().getTime());
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
    public List<Absence> getAllCourseAbsencesById(@PathVariable String courseId) {
        LOG.info("getAllCourseAbsences - starting - courseId: {}", courseId);

        List<Absence> absences = absenceRepository.findByCourseId(courseId);

        return absences;
    }

    @GetMapping(value = "/courses/{courseId}/absences/today")
    public List<Absence> getAllCourseAbsencesForTodayById(@PathVariable String courseId) {
        LOG.info("getAllCourseAbsencesForToday - starting - courseId: {}", courseId);

        java.sql.Date today = new java.sql.Date(new java.util.Date().getTime());

        List<Absence> absences = absenceRepository.findByCourseIdAndDate(courseId, today);

        return absences;
    }

    @GetMapping(value = "/courses/{courseId}/absences/{student}")
    public List<Absence> getAllStudentAbsencesById(@PathVariable String courseId, @PathVariable String studentId) {
        LOG.info("getAllStudentAbsences - starting - courseId: {}, student: {}", courseId, studentId);

        java.sql.Date today = new java.sql.Date(new java.util.Date().getTime());

        List<Absence> absences = absenceRepository.findByCourseIdAndStudentId(courseId, studentId);

        return absences;

    }
}
