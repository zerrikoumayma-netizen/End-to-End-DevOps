package com.example.demo.controllers;

import com.example.demo.*;
import com.example.demo.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final ReactionService reactionService;
    private final TagRepository tagRepository;

    @Autowired
    public PostController(PostService postService, UserService userService,
                          CommentService commentService, ReactionService reactionService,
                          TagRepository tagRepository ) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.reactionService = reactionService;
        this.tagRepository = tagRepository;
    }

    // ── POSTS ─────────────────────────────────────────────────

    @GetMapping("/posts")
    public String listPosts(@RequestParam(required = false) String author, Model model, Principal principal) {
        User currentUser = (principal != null) ? userService.findByUsername(principal.getName()) : null;
        if (author != null && !author.isBlank()) {
            User authorUser = userService.findByUsername(author);
            if (authorUser != null) {
                model.addAttribute("posts", postService.getPostsByAuthor(authorUser));
                model.addAttribute("filterAuthor", authorUser);
            } else {
                model.addAttribute("posts", postService.getAllPosts());
            }
        } else {
            model.addAttribute("posts", postService.getAllPosts());
        }
        model.addAttribute("currentUser", currentUser);
        return "list";
    }

    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.getPostById(id);
        User currentUser = (principal != null) ? userService.findByUsername(principal.getName()) : null;

        if (currentUser != null) {
            postService.incrementViews(post, currentUser);
        }

        Map<String, Object> likeStatus = postService.getLikeStatus(post, currentUser);
        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.getRootCommentsByPost(post));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("likeStatus", likeStatus);
        return "view";
    }

    // AJOUT : Méthode pour voir le profil d'un utilisateur
    @GetMapping("/profile/{id}")
    public String showProfile(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.getUserById(id); // Assurez-vous que cette méthode existe dans UserService
        User currentUser = (principal != null) ? userService.findByUsername(principal.getName()) : null;

        model.addAttribute("user", user);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("posts", postService.getPostsByAuthor(user));
        return "profile"; // Créez un fichier profile.html
    }

    @GetMapping("/posts/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "form";
    }

    @PostMapping("/posts")
    public String createPost(@RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(defaultValue = "article") String postType,
                             @RequestParam(required = false) String summary,
                             @RequestParam(required = false) String url,
                             @RequestParam(required = false) String tags,
                             Principal principal) {
        User author = userService.findByUsername(principal.getName());

        Post post;
        if ("video".equals(postType)) {
            VideoPost vp = new VideoPost();
            vp.setUrl(url);
            post = vp;
        } else {
            Article article = new Article();
            article.setSummary(summary);
            post = article;
        }

        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);

        // Gestion des Tags : "spring,java" -> cherche ou crée
        if (tags != null && !tags.isBlank()) {
            Set<Tag> tagSet = new HashSet<>();
            for (String tagName : tags.split(",")) {
                String name = tagName.trim().toLowerCase();
                if (!name.isEmpty()) {
                    Tag tag = tagRepository.findByName(name)
                            .orElseGet(() -> {
                                Tag t = new Tag();
                                t.setName(name);
                                return tagRepository.save(t);
                            });
                    tagSet.add(tag);
                }
            }
            post.setTags(tagSet);
        }

        postService.createPost(post);
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "form";
    }

    @PostMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post) {
        Post existing = postService.getPostById(id);
        existing.setTitle(post.getTitle());
        existing.setContent(post.getContent());
        postService.updatePost(existing);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    // ── RÉACTIONS ─────────────────────────────────────────────

    @PostMapping("/posts/{id}/react")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> react(@PathVariable Long id,
                                                     @RequestBody Map<String, String> body,
                                                     Principal principal) {
        Post post = postService.getPostById(id);
        User user = userService.findByUsername(principal.getName());
        ReactionType type = ReactionType.valueOf(body.get("type"));
        Map<String, Object> result = reactionService.toggleReaction(post, user, type);
        return ResponseEntity.ok(result);
    }

    // ── COMMENTAIRES ──────────────────────────────────────────

    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable Long id, @RequestParam("content") String content, Principal principal) {
        Post post = postService.getPostById(id);
        User author = userService.findByUsername(principal.getName());
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setAuthor(author);
        commentService.addComment(comment);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/reply")
    public String replyToComment(@PathVariable Long postId, @PathVariable Long commentId,
                                 @RequestParam("content") String content, Principal principal) {
        Post post = postService.getPostById(postId);
        User author = userService.findByUsername(principal.getName());
        commentService.addReply(commentId, content, post, author);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }

    // ── LIKES COMMENTAIRES ────────────────────────────────────

    @PostMapping("/comments/{commentId}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likeComment(@PathVariable Long commentId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(commentService.toggleLike(commentId, user));
    }

    @PostMapping("/comments/{commentId}/dislike")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> dislikeComment(@PathVariable Long commentId, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        return ResponseEntity.ok(commentService.toggleDislike(commentId, user));
    }
}
