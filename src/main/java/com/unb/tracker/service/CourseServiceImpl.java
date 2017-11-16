package com.unb.tracker.service;

import com.unb.tracker.model.Course;
import com.unb.tracker.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void save(Course course) {
        courseRepository.save(course);
    }

    @Override
    public List<Course> findByInstructorUsernameAndNameAndSection(String username, String name, String section) {
        return courseRepository.findByInstructorUsernameAndNameAndSection(username, name, section);
    }
}
