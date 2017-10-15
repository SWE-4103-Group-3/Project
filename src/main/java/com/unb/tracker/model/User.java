package com.unb.tracker.model;

import javax.persistence.*;

@Entity // This tells Hibernate to make a table out of this class
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private String username;

    private String email;

    private String password;

    @Transient
    private String passwordConfirm;

    private boolean hasExtendedPrivileges;

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
}
