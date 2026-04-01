package com.example.demo;

import com.example.demo.repositories.PostRepository;
import com.example.demo.repositories.PostViewRepository;
import com.example.demo.repositories.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostViewRepository postViewRepository;
    private final ReactionRepository reactionRepository;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostViewRepository postViewRepository,
                       ReactionRepository reactionRepository) {
        this.postRepository = postRepository;
        this.postViewRepository = postViewRepository;
        this.reactionRepository = reactionRepository;
    }

    // ── CRUD ──────────────────────────────────────────────────

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> getPostsByAuthor(User author) {
        return postRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    // ── VUES ──────────────────────────────────────────────────

    public void incrementViews(Post post, User user) {
        boolean alreadyViewed = postViewRepository.existsByPostAndUser(post, user);
        if (!alreadyViewed) {
            PostView view = new PostView();
            view.setPost(post);
            view.setUser(user);
            postViewRepository.save(view);
            post.setViews((int) postViewRepository.countByPost(post));
            postRepository.save(post);
        }
    }

    // ── RÉACTIONS (remplace PostLike) ─────────────────────────

    /**
     * Ajoute ou change la réaction d'un utilisateur sur un post.
     * - Si même réaction → annule (supprime)
     * - Si réaction différente → change
     * - Si aucune réaction → ajoute
     */
    public Map<String, Object> react(Post post, User user, ReactionType type) {
        Optional<Reaction> existing = reactionRepository.findByUserAndPost(user, post);

        if (existing.isPresent()) {
            Reaction reaction = existing.get();
            if (reaction.getType() == type) {
                // même réaction → annuler
                reactionRepository.delete(reaction);
            } else {
                // réaction différente → changer
                reaction.setType(type);
                reactionRepository.save(reaction);
            }
        } else {
            // nouvelle réaction
            Reaction reaction = new Reaction();
            reaction.setPost(post);
            reaction.setUser(user);
            reaction.setType(type);
            reactionRepository.save(reaction);
        }

        return buildReactionResult(post, user);
    }

    // Récupère l'état des réactions pour un post + un utilisateur
    public Map<String, Object> getLikeStatus(Post post, User user) {
        return buildReactionResult(post, user);
    }

    private Map<String, Object> buildReactionResult(Post post, User user) {
        List<Reaction> allReactions = reactionRepository.findByPost(post);

        // Compter chaque type de réaction
        Map<String, Long> counts = new HashMap<>();
        for (ReactionType type : ReactionType.values()) {
            long count = allReactions.stream()
                    .filter(r -> r.getType() == type)
                    .count();
            counts.put(type.name(), count);
        }

        // Réaction de l'utilisateur connecté
        String userReaction = "NONE";
        Optional<Reaction> userR = reactionRepository.findByUserAndPost(user, post);
        if (userR.isPresent()) {
            userReaction = userR.get().getType().name();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("counts", counts);       // ex: {LIKE=3, LOVE=1, HAHA=0 ...}
        result.put("userReaction", userReaction); // ex: "LIKE" ou "NONE"
        result.put("total", allReactions.size());
        return result;
    }

    public Map<String, Object> toggleLike(Post post, User user) {
        return Map.of();
    }

    public Map<String, Object> toggleDislike(Post post, User user) {
        return Map.of();
    }
}