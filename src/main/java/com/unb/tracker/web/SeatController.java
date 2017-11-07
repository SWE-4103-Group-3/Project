package com.unb.tracker.web;

import com.unb.tracker.exception.BadRequestException;
import com.unb.tracker.exception.InternalServerErrorException;
import com.unb.tracker.exception.NotFoundException;
import com.unb.tracker.model.Course;
import com.unb.tracker.model.Seat;
import com.unb.tracker.model.User;
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
    private UserRepository userRepository;

    @PostMapping(value = "/seats")
    public @ResponseBody
    String postCourseSeat(@RequestBody Seat seat, Principal principal) {
        LOG.info("postCourseSeat - starting");

        User user = userRepository.findByUsername(principal.getName());
        if(seat.getStudent() != null && user.getId() != seat.getStudent().getId()) {
            throw new BadRequestException();
        }

        Course course = seat.getCourse();
        List<Seat> seats = course.getSeats();

        for(Seat s : seats) {
            if(s.getId() == seat.getId() && s.getStudent().getId() == seat.getStudent().getId()){
                seat.removeStudent();
                return "removed";
            }
        }

        for(Seat s : seats) {
            if(s.getStudent().getId() == seat.getStudent().getId()) {
                throw new InternalServerErrorException();
            }
        }
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
