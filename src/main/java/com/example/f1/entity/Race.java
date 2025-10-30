package com.example.f1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "race")
public class Race {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "race_id")
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private Integer season;
    
    @NotNull
    @Column(nullable = false)
    private Integer round;
    
    @Column(name = "circuit_name", length = 100)
    private String circuitName;
    
    @Column(name = "race_date")
    private LocalDateTime raceDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RaceStatus status = RaceStatus.SCHEDULED;
    
    // Constructors
    public Race() {}
    
    public Race(Integer season, Integer round) {
        this.season = season;
        this.round = round;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getSeason() {
        return season;
    }
    
    public void setSeason(Integer season) {
        this.season = season;
    }
    
    public Integer getRound() {
        return round;
    }
    
    public void setRound(Integer round) {
        this.round = round;
    }
    
    public String getCircuitName() {
        return circuitName;
    }
    
    public void setCircuitName(String circuitName) {
        this.circuitName = circuitName;
    }
    
    public LocalDateTime getRaceDate() {
        return raceDate;
    }
    
    public void setRaceDate(LocalDateTime raceDate) {
        this.raceDate = raceDate;
    }
    
    public RaceStatus getStatus() {
        return status;
    }
    
    public void setStatus(RaceStatus status) {
        this.status = status;
    }
    
    public enum RaceStatus {
        SCHEDULED, LIVE, FINISHED
    }
}

