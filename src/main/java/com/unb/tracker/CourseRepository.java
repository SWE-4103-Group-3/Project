package com.unb.tracker;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CourseRepository extends CrudRepository<Course, Long> {
    public List<Course> findByName(String name);
    public List<Course> findByNameAndSection(String name, String section);
}
