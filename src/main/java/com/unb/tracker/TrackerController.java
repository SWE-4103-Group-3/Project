package com.unb.tracker;

import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackerController {
    @RequestMapping(value="/", method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    public String index(ModelMap map) {
        return "<html><h1>SWE 4103 Project</h1></html>";
    }
}
