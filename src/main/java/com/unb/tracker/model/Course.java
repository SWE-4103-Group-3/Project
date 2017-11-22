package com.unb.tracker.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


@Entity // This tells Hibernate to make a table out of this class
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date startDate;
    private String name;
    private String section;
    private Integer rows;
    private Integer cols;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Seat> seats;

    @ManyToOne(fetch = FetchType.EAGER)
    private User instructor;

    @Transient
    private Long courseGridReuseID;

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getCols() {
        return cols;
    }

    public void setCols(Integer cols) {
        this.cols = cols;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    public Long getCourseGridReuseID() {
        return courseGridReuseID;
    }

    @Transient
    public void setCourseGridReuseID(Long courseGridReuseID) {
        this.courseGridReuseID = courseGridReuseID;
    }

    public void removeSeats(List<Seat> seatsToRemove) {
        seats.removeAll(seatsToRemove);
        for(Seat s : seatsToRemove) {
            s.setCourse(null);
        }
    }

    public List<Seat> getOutOfBoundsSeats(int rows, int cols) {
        List<Seat> seatsToRemove = new ArrayList<>();
        if(seats != null) {
            for(Seat s : seats) {
                if(s.getRow() >= rows || s.getCol() >= cols) {
                    seatsToRemove.add(s);
                }
            }
        }
        return seatsToRemove;
    }
}
