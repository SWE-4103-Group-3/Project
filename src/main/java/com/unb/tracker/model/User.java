package com.unb.tracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity // This tells Hibernate to make a table out of this class
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;

    private String email;

    private String password;

    @Transient
    private String passwordConfirm;

    private boolean hasExtendedPrivileges;

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Seat> seats; // for students

    @OneToMany(mappedBy = "instructor", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Course> courses; // for instructors

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course", fetch = FetchType.EAGER)

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Transient
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    @Transient
    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public boolean getHasExtendedPrivileges() {
        return hasExtendedPrivileges;
    }

    public void setHasExtendedPrivileges(boolean hasExtendedPrivileges) {
        this.hasExtendedPrivileges = hasExtendedPrivileges;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    /**
     * This method returns the courses created by the user if they're an instructor or the courses they're currently registered in if they're a student
     * @return their courses
     */
    public Iterable<Course> getCourses() {
        if(this.getHasExtendedPrivileges()) {
            return this.courses;
        } else {
            if(seats == null) {
                return null;
            }

            List<Course> courses = new ArrayList<>();
            for(Seat s : seats) {
                courses.add(s.getCourse());
            }
            return courses;
        }
    }
}
