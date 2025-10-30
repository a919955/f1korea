package com.example.f1.repository;

import com.example.f1.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    List<Driver> findByTeamId(Long teamId);
    List<Driver> findByNationality(String nationality);
}

