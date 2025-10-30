package com.example.f1.repository;

import com.example.f1.entity.Race;
import com.example.f1.entity.Race.RaceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {
    List<Race> findBySeason(Integer season);
    List<Race> findByStatus(RaceStatus status);
    List<Race> findBySeasonAndStatus(Integer season, RaceStatus status);
}

