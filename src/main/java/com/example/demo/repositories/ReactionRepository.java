package com.example.demo.repositories;

import com.example.demo.Post;
import com.example.demo.Reaction;
import com.example.demo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndPost(User user, Post post);
    List<Reaction> findByPost(Post post);
}