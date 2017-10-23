package com.unb.tracker.repository;


import com.unb.tracker.model.Course;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CourseRepository extends CrudRepository<Course, Long> {
    public List<Course> findByInstructorUsernameAndNameAndSection(String username, String name, String section);
    public List<Course> findByInstructorUsernameAndName(String username, String name);
}
