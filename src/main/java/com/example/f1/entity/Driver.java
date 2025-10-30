package com.example.f1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
@Table(name = "driver")
public class Driver {
    
    @Id
    @Column(name = "driver_id", length = 50)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    
    @NotBlank
    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;
    
    @NotBlank
    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;
    
    @Column(name = "nationality", length = 50)
    private String nationality;
    
    @Column(name = "birthdate")
    private LocalDate birthdate;
    
    @Column(name = "car_number")
    private Integer carNumber;
    
    // Constructors
    public Driver() {}
    
    public Driver(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Team getTeam() {
        return team;
    }
    
    public void setTeam(Team team) {
        this.team = team;
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
    
    public String getNationality() {
        return nationality;
    }
    
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    
    public LocalDate getBirthdate() {
        return birthdate;
    }
    
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }
    
    public Integer getCarNumber() {
        return carNumber;
    }
    
    public void setCarNumber(Integer carNumber) {
        this.carNumber = carNumber;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

