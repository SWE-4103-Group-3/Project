package com.unb.tracker;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Controller
@EnableAutoConfiguration
public class TrackerController {
    @RequestMapping(value="/", method = RequestMethod.GET)
    public String index(ModelMap map) {
        return "index";
    }
}
