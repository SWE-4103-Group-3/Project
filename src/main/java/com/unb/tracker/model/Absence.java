package com.unb.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User student;


    @ManyToOne(fetch = FetchType.EAGER)
    @JsonManagedReference
    private Course course;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course1) {
        this.course = course1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student1) {
        this.student = student1;
    }
}
