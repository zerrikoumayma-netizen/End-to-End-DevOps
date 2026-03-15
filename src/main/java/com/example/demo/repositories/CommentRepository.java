package com.example.demo.repositories;

import com.example.demo.Comment;
import com.example.demo.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Commentaires racines seulement (sans parent)
    List<Comment> findByPostAndParentIsNullOrderByCreatedAtDesc(Post post);

    // Réponses à un commentaire
    List<Comment> findByParentOrderByCreatedAtAsc(Comment parent);
}