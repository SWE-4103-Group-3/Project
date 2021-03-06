package com.unb.tracker.web;

import com.unb.tracker.exception.BadRequestException;
import com.unb.tracker.exception.InternalServerErrorException;
import com.unb.tracker.exception.NotFoundException;
import com.unb.tracker.model.Course;
import com.unb.tracker.model.Seat;
import com.unb.tracker.model.User;
import com.unb.tracker.repository.CourseRepository;
import com.unb.tracker.repository.SeatRepository;
import com.unb.tracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class SeatController {
    private static final Logger LOG = LoggerFactory.getLogger(SeatController.class);

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/seats")
    public @ResponseBody String postSeat(@RequestBody Seat seat, Principal principal) {
        LOG.info("postSeat - starting - seat.id: {}", seat.getId());
        User user = userRepository.findByUsername(principal.getName());

        if(seat.getStudent() == null) {
            LOG.warn("student must not be null");
            throw new BadRequestException();
        }

        LOG.debug("seat state: {}", seat.getState());
        if(seatRepository.findOne(seat.getId()).getState() != Seat.AVAILABLE) {
            LOG.warn("student {} is trying to choose an unavailable seat", user.getUsername());
            throw new BadRequestException();
        }

        if(!user.getId().equals(seat.getStudent().getId())) {
            LOG.warn("{} trying to alter {}'s seat", user.getUsername(), seat.getStudent().getUsername());
            throw new BadRequestException();
        }

        if(seat.getCourse() == null) {
            LOG.warn("{} sent in seat with no course", user.getUsername());
            throw new BadRequestException();
        }

        Long courseId = seat.getCourse().getId();
        Course course = courseRepository.findOne(courseId);
        List<Seat> seats = course.getSeats();

        // Lets remove the student from all of the seats first
        for(Seat s : seats) {
            if(s.getStudent() == null) {
                continue;
            } else if(s.getStudent().getId().equals(user.getId())) {
                LOG.debug("removing {} from seat {}", user.getUsername(), s.getId());
                s.removeStudent();
                seatRepository.save(s);
            }
        }

        LOG.debug("saving {} to seat {}", user.getUsername(), seat.getId());
        seatRepository.save(seat);

        user.getSeats().add(seat);
        userRepository.save(user);

        return "saved";
    }

    @PostMapping("/seats/{seatId}/student/remove")
    public @ResponseBody String removeStudentFromSeat(@PathVariable Long seatId, Principal principal) {
        LOG.info("removeStudentFromSeat - starting - seat.id: {}", seatId);
        User user = userRepository.findByUsername(principal.getName());

        if(!user.getHasExtendedPrivileges()) {
            LOG.warn("{} trying to remove student from seat", user.getUsername());
            throw new BadRequestException();
        }

        Seat seat = seatRepository.findOne(seatId);
        if(seat == null) {
            throw new NotFoundException();
        }

        LOG.debug("removing {} from their seat", seat.getStudent() == null ? null : seat.getStudent().getUsername());
        seat.removeStudent();
        seatRepository.save(seat);

        return "saved";
    }

    @GetMapping("/seats/{seatId}")
    public @ResponseBody Seat getSeat(@PathVariable Long seatId) {
        LOG.info("getSeat - starting - seatId: {}", seatId);
        Seat seat = seatRepository.findOne(seatId);
        if (seat == null) {
            throw new NotFoundException();
        }
        return seat;
    }

    @GetMapping("/seats")
    public @ResponseBody Iterable<Seat> getSeats() {
        LOG.info("getSeats - starting");
        Iterable<Seat> seats = seatRepository.findAll();
        return seats;
    }
}
