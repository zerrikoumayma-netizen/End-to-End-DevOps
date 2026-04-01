package com.example.demo;

import com.example.demo.repositories.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;

    @Autowired
    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    /**
     * Si l'utilisateur a déjà cette réaction → annule
     * Si l'utilisateur a une autre réaction → change
     * Sinon → ajoute
     */
    public Map<String, Object> toggleReaction(Post post, User user, ReactionType type) {
        Optional<Reaction> existing = reactionRepository.findByUserAndPost(user, post);

        if (existing.isPresent()) {
            Reaction r = existing.get();
            if (r.getType() == type) {
                reactionRepository.delete(r); // annule
            } else {
                r.setType(type); // change
                reactionRepository.save(r);
            }
        } else {
            Reaction r = new Reaction();
            r.setPost(post);
            r.setUser(user);
            r.setType(type);
            reactionRepository.save(r);
        }

        return buildResult(post, user);
    }

    private Map<String, Object> buildResult(Post post, User user) {
        List<Reaction> reactions = reactionRepository.findByPost(post);
        Optional<Reaction> userReaction = reactionRepository.findByUserAndPost(user, post);

        Map<String, Object> result = new HashMap<>();
        result.put("total", reactions.size());
        result.put("userReaction", userReaction.map(r -> r.getType().name()).orElse("NONE"));
        return result;
    }
}