package com.example.VaadinMySQL.model;


import com.example.VaadinMySQL.model.reports.Reportable;

import java.util.ArrayList;

public class Customer extends Reportable {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String joinDate;

    public Customer(){
    }

    public Customer(Long id, String firstName, String lastName, String email, String joinDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.joinDate = joinDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public boolean isPersisted() {
        return id != null;
    }

    @Override
    public ArrayList<String> toStringArray() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(this.firstName);
        stringArrayList.add(this.lastName);
        stringArrayList.add(this.email);
        stringArrayList.add(this.joinDate);

        return stringArrayList;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", joinDate='" + joinDate + '\'' +
                '}';
    }


}
