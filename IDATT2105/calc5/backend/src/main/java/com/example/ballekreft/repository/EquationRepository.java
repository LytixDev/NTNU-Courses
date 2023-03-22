package com.example.ballekreft.repository;

import com.example.ballekreft.model.Equation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquationRepository extends JpaRepository<Equation, Long> {
}
