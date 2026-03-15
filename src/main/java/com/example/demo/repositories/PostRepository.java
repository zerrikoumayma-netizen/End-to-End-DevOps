package com.example.demo.repositories;

import com.example.demo.Post;
import com.example.demo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // AJOUTÉ : posts filtrés par auteur
    List<Post> findByAuthorOrderByCreatedAtDesc(User author);
    List<Post> findAllByOrderByCreatedAtDesc();
}