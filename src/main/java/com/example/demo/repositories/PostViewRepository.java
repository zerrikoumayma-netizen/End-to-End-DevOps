package com.example.demo.repositories;

import com.example.demo.Post;
import com.example.demo.PostView;
import com.example.demo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostViewRepository extends JpaRepository<PostView, Long> {

    // Vérifie si cet utilisateur a déjà vu ce post
    boolean existsByPostAndUser(Post post, User user);

    // Compte le nombre de vues uniques d'un post
    long countByPost(Post post);
}