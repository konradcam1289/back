package com.workshopapp.workshopservice.repository;

import com.workshopapp.workshopservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByActiveTrue();
    List<User> findByActiveFalse();
}
