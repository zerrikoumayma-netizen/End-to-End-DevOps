package com.example.demo;

import com.example.demo.repositories.PostLikeRepository;
import com.example.demo.repositories.PostRepository;
import com.example.demo.repositories.PostViewRepository;
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
    private final PostLikeRepository postLikeRepository;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostViewRepository postViewRepository,
                       PostLikeRepository postLikeRepository) {
        this.postRepository = postRepository;
        this.postViewRepository = postViewRepository;
        this.postLikeRepository = postLikeRepository;
    }

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

    // ── VUES ─────────────────────────────────────────────────

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

    // ── LIKES ─────────────────────────────────────────────────

    /**
     * Retourne : likes, dislikes, userAction ("like" | "dislike" | "none")
     *
     * Comportement :
     * - Si l'utilisateur n'a pas encore liké → like (+1)
     * - Si l'utilisateur a déjà liké → annule le like (unlike)
     * - Si l'utilisateur a déjà disliké → passe en like
     */
    public Map<String, Object> toggleLike(Post post, User user) {
        Optional<PostLike> existing = postLikeRepository.findByPostAndUser(post, user);

        if (existing.isPresent()) {
            PostLike postLike = existing.get();
            if (postLike.isLiked()) {
                // déjà liké → annuler le like
                postLikeRepository.delete(postLike);
            } else {
                // avait disliké → passe en like
                postLike.setLiked(true);
                postLikeRepository.save(postLike);
            }
        } else {
            // pas encore de réaction → like
            PostLike postLike = new PostLike();
            postLike.setPost(post);
            postLike.setUser(user);
            postLike.setLiked(true);
            postLikeRepository.save(postLike);
        }

        return buildLikeResult(post, user);
    }

    /**
     * Comportement dislike :
     * - Si l'utilisateur n'a pas encore réagi → dislike
     * - Si l'utilisateur a déjà disliké → annule le dislike
     * - Si l'utilisateur a déjà liké → passe en dislike
     */
    public Map<String, Object> toggleDislike(Post post, User user) {
        Optional<PostLike> existing = postLikeRepository.findByPostAndUser(post, user);

        if (existing.isPresent()) {
            PostLike postLike = existing.get();
            if (!postLike.isLiked()) {
                // déjà disliké → annuler le dislike
                postLikeRepository.delete(postLike);
            } else {
                // avait liké → passe en dislike
                postLike.setLiked(false);
                postLikeRepository.save(postLike);
            }
        } else {
            // pas encore de réaction → dislike
            PostLike postLike = new PostLike();
            postLike.setPost(post);
            postLike.setUser(user);
            postLike.setLiked(false);
            postLikeRepository.save(postLike);
        }

        return buildLikeResult(post, user);
    }

    // Retourne les compteurs et l'état de l'utilisateur
    private Map<String, Object> buildLikeResult(Post post, User user) {
        long likes = postLikeRepository.countByPostAndLiked(post, true);
        long dislikes = postLikeRepository.countByPostAndLiked(post, false);
        Optional<PostLike> userReaction = postLikeRepository.findByPostAndUser(post, user);

        String userAction = "none";
        if (userReaction.isPresent()) {
            userAction = userReaction.get().isLiked() ? "like" : "dislike";
        }

        // Met à jour les compteurs dans Post
        post.setLikes((int) likes);
        postRepository.save(post);

        Map<String, Object> result = new HashMap<>();
        result.put("likes", likes);
        result.put("dislikes", dislikes);
        result.put("userAction", userAction);
        return result;
    }

    // Récupère l'état like/dislike d'un utilisateur sur un post
    public Map<String, Object> getLikeStatus(Post post, User user) {
        return buildLikeResult(post, user);
    }
}