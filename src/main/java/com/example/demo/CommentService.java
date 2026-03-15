package com.example.demo;

import com.example.demo.repositories.CommentLikeRepository;
import com.example.demo.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                          CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    // Ajouter un commentaire racine
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // Répondre à un commentaire
    public Comment addReply(Long parentId, String content, Post post, User author) {
        Comment parent = commentRepository.findById(parentId).orElse(null);
        if (parent == null) return null;

        Comment reply = new Comment();
        reply.setContent(content);
        reply.setPost(post);
        reply.setAuthor(author);
        reply.setParent(parent);
        return commentRepository.save(reply);
    }

    // Commentaires racines d'un post (avec leurs réponses chargées)
    public List<Comment> getRootCommentsByPost(Post post) {
        return commentRepository.findByPostAndParentIsNullOrderByCreatedAtDesc(post);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    // ── LIKES COMMENTAIRES ────────────────────────────────────

    public Map<String, Object> toggleLike(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) return Map.of("likes", 0, "dislikes", 0, "userAction", "none");
        return toggle(comment, user, true);
    }

    public Map<String, Object> toggleDislike(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) return Map.of("likes", 0, "dislikes", 0, "userAction", "none");
        return toggle(comment, user, false);
    }

    private Map<String, Object> toggle(Comment comment, User user, boolean isLike) {
        Optional<CommentLike> existing = commentLikeRepository.findByCommentAndUser(comment, user);

        if (existing.isPresent()) {
            CommentLike cl = existing.get();
            if (cl.isLiked() == isLike) {
                // même réaction → annuler
                commentLikeRepository.delete(cl);
            } else {
                // réaction opposée → changer
                cl.setLiked(isLike);
                commentLikeRepository.save(cl);
            }
        } else {
            CommentLike cl = new CommentLike();
            cl.setComment(comment);
            cl.setUser(user);
            cl.setLiked(isLike);
            commentLikeRepository.save(cl);
        }

        return buildResult(comment, user);
    }

    private Map<String, Object> buildResult(Comment comment, User user) {
        long likes = commentLikeRepository.countByCommentAndLiked(comment, true);
        long dislikes = commentLikeRepository.countByCommentAndLiked(comment, false);
        Optional<CommentLike> reaction = commentLikeRepository.findByCommentAndUser(comment, user);

        String userAction = "none";
        if (reaction.isPresent()) {
            userAction = reaction.get().isLiked() ? "like" : "dislike";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("likes", likes);
        result.put("dislikes", dislikes);
        result.put("userAction", userAction);
        result.put("commentId", comment.getId());
        return result;
    }

    public Map<String, Object> getLikeStatus(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) return Map.of("likes", 0, "dislikes", 0, "userAction", "none");
        return buildResult(comment, user);
    }
}