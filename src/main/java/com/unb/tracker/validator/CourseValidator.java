package com.unb.tracker.validator;

import com.unb.tracker.model.Course;
import com.unb.tracker.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CourseValidator implements Validator {
    @Autowired
    private CourseService courseService;

    @Override
    public boolean supports(Class<?> aClass) {
        return Course.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Course course = (Course) o;

        if (course.getRows() < 1 || course.getRows() > 18) {
            errors.rejectValue("rows", "Please enter between 1 and 18 rows.");
        }

        if (course.getCols() < 1 || course.getCols() > 31) {
            errors.rejectValue("cols", "Please enter between 1 and 31 columns.");
        }
    }
}
