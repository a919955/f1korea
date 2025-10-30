package com.example.f1.repository;

import com.example.f1.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByCountry(String country);
    List<Team> findByFoundedYear(Integer foundedYear);
}

