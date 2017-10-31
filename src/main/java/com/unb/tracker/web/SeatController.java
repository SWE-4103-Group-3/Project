package com.unb.tracker.web;

import com.unb.tracker.exception.NotFoundException;
import com.unb.tracker.model.Course;
import com.unb.tracker.model.Seat;
import com.unb.tracker.repository.SeatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@EnableAutoConfiguration
public class SeatController {
    private static final Logger LOG = LoggerFactory.getLogger(SeatController.class);

    @Autowired
    private SeatRepository seatRepository;

    @PostMapping(value = "/seats")
    public @ResponseBody
    String postCourseSeat(@RequestBody Seat seat, Principal principal) {
        LOG.info("postCourseSeat - starting");
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
}
