package com.example.demo;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
@Entity
@Table(name = "users")  // ← ajouter cette ligne !

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;
    private String role; // ← ajouter ce champ !

    // Getters et setters existants +
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }



    // ... reste des getters/setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}