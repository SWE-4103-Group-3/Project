package com.unb.tracker.repository;

import com.unb.tracker.model.Absence;
import org.hibernate.annotations.OrderBy;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.util.List;

public interface AbsenceRepository extends CrudRepository<Absence, Long> {
    @OrderBy(clause = "name DESC")
    public List<Absence> findByCourseId(Long courseId);

    List<Absence> findByCourseIdAndDate(Long courseId, Date date);

    List<Absence> findByCourseIdAndStudentId(Long courseId, Long studentId);

}
