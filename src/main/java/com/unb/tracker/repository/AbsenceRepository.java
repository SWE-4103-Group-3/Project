package com.unb.tracker.repository;

import com.unb.tracker.model.Absence;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface AbsenceRepository extends CrudRepository<Absence, Long> {
    public List<Absence> findByCourseId(String courseId);

    public List<Absence> findByCourseIdAndDate(String courseID, Date date);

    public List<Absence> findByCourseIdAndStudentId(String courseID, String studentId);

}
