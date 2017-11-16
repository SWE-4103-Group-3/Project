package com.unb.tracker.service;

import com.unb.tracker.model.Course;

import java.util.List;

public interface CourseService {
    void save(Course course);

    List<Course> findByInstructorUsernameAndNameAndSection(String username, String name, String section);
}
