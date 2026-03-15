package com.example.demo.repositories;

import com.example.demo.Comment;
import com.example.demo.CommentLike;
import com.example.demo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
    long countByCommentAndLiked(Comment comment, boolean liked);
}