package com.unb.tracker.model;


import javax.persistence.*;
import java.sql.Date;
import java.util.List;


@Entity // This tells Hibernate to make a table out of this class
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //TODO: Add link to users table when it actually exists
    //private User Professor;

    //TODO: Add link to seat plan table when it actually exists
    //private SeatPlan seatPlan

    //TODO: Investigate how to keep "Old seat Plan (another FK?)"
    //private SeatPlan oldSeatPlan

    private String timeSlot;
    private Date startDate;
    private Date endDate;
    private String name;
    private String section;
    private Integer rows;
    private Integer cols;
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    //necessary for hibernate when updating course that does not already have a seat plan
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

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
}
