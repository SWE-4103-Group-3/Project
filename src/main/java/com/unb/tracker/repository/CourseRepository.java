package com.unb.tracker.repository;


import com.unb.tracker.model.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends CrudRepository<Course, Long> {
    public List<Course> findByInstructorUsernameAndNameAndSection(String username, String name, String section);

    public List<Course> findByInstructorUsernameAndName(String username, String name);

    @Query(value = "SELECT * FROM course WHERE course.name LIKE CONCAT('%',:pname,'%') OR course.section LIKE CONCAT('%',:pname,'%') LIMIT 10", nativeQuery = true)
    public List<Course> findByPartialName(@Param("pname")String partialName);

    public Course findById(Long id);

}
