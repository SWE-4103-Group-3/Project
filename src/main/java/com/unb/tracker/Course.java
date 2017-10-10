package com.unb.tracker;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

@Entity // This tells Hibernate to make a table out of this class
public class Course {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    
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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTimeSlot() { return timeSlot; }

    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

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


}
