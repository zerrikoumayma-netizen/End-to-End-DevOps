package com.example.demo.repositories;

import com.example.demo.Post;
import com.example.demo.PostLike;
import com.example.demo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // Trouver le like/dislike d'un utilisateur sur un post
    Optional<PostLike> findByPostAndUser(Post post, User user);

    // Compter les likes (liked = true)
    long countByPostAndLiked(Post post, boolean liked);
}