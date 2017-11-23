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
        
        if (course.getId() == null && !courseService.findByInstructorUsernameAndNameAndSection(course.getInstructor().getUsername(), course.getName(), course.getSection()).isEmpty()) {
            errors.rejectValue("name", "This section for this course already exists.");
        }

        if (course.getStartDate() == null) {
            errors.rejectValue("startDate", "Ensure all required fields are filled. (Check Start Date)");
        }

        if (course.getName() == null || course.getName().isEmpty()) {
            errors.rejectValue("name", "Ensure all required fields are filled. (Check Course Name)");
        } else if (course.getName().length() > 10) {
            errors.rejectValue("name", "Course name should be less than 10 characters");
        }

        if (course.getSection() != null && course.getSection().length() > 10) {
            errors.rejectValue("section", "Section should be less than 10 characters");
        }

        if (course.getRows() == null) {
            errors.rejectValue("rows", "Ensure all required fields are filled. (Check Rows)");
        } else if (course.getRows() < 1 || course.getRows() > 18) {
            errors.rejectValue("rows", "Please enter between 1 and 18 rows.");
        }

        if (course.getCols() == null) {
            errors.rejectValue("cols", "Ensure all required fields are filled. (Check Columns)");
        } else if (course.getCols() < 1 || course.getCols() > 31) {
            errors.rejectValue("cols", "Please enter between 1 and 31 columns.");
        }
    }
}
