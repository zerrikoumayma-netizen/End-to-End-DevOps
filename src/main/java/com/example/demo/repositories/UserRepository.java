package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring génère automatiquement les opérations CRUD !
    User findByUsername(String username);
    User findByEmail(String email);
}