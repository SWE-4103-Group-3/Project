package com.unb.tracker.validator;

import com.unb.tracker.model.Course;
import com.unb.tracker.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
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

        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"name", "Ensure all required fields are filled. (Check Course Name)");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"rows", "Ensure all required fields are filled. (Check Rows)");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"cols", "Ensure all required fields are filled. (Check Columns)");
        
        if (!courseService.findByInstructorUsernameAndNameAndSection(course.getInstructor().getUsername(), course.getName(), course.getSection()).isEmpty()) {
            errors.rejectValue("name", "This section for this course already exists.");
        }

        if (course.getStartDate().toString().equals("1970-01-01")) {
            errors.rejectValue("startDate", "Ensure all required fields are filled. (Check Start Date)");
        }

        if (course.getName().length() > 10) {
            errors.rejectValue("name", "Course name should be less than 10 characters");
        }

        if (course.getSection().length() > 10) {
            errors.rejectValue("section", "Section should be less than 10 characters");
        }

        if (course.getRows() < 1 || course.getRows() > 18) {
            errors.rejectValue("rows", "Please enter between 1 and 18 rows.");
        }

        if (course.getCols() < 1 || course.getCols() > 31) {
            errors.rejectValue("cols", "Please enter between 1 and 31 columns.");
        }


    }
}
