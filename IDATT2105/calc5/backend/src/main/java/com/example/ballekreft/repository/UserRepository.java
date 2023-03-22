package com.example.ballekreft.repository;

import com.example.ballekreft.model._User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<_User, String> {

    Optional<_User> findByUsername(String username);

}
