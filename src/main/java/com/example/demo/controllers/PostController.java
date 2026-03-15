package com.example.demo.controllers;

import com.example.demo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    // ── POSTS ────────────────────────────────────────────────

    @GetMapping("/posts")
    public String listPosts(@RequestParam(required = false) String author, Model model, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
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
        User currentUser = userService.findByUsername(principal.getName());

        postService.incrementViews(post, currentUser);

        // récupère l'état like/dislike de l'utilisateur connecté
        Map<String, Object> likeStatus = postService.getLikeStatus(post, currentUser);

        model.addAttribute("post", post);
        model.addAttribute("comments", commentService.getCommentsByPost(post));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("likeStatus", likeStatus);
        return "view";
    }

    @GetMapping("/posts/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "form";
    }

    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post, Principal principal) {
        User author = userService.findByUsername(principal.getName());
        post.setAuthor(author);
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

    // ── LIKES / DISLIKES ─────────────────────────────────────

    @PostMapping("/posts/{id}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long id, Principal principal) {
        Post post = postService.getPostById(id);
        User user = userService.findByUsername(principal.getName());
        Map<String, Object> result = postService.toggleLike(post, user);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/posts/{id}/dislike")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> dislikePost(@PathVariable Long id, Principal principal) {
        Post post = postService.getPostById(id);
        User user = userService.findByUsername(principal.getName());
        Map<String, Object> result = postService.toggleDislike(post, user);
        return ResponseEntity.ok(result);
    }

    // ── COMMENTAIRES ─────────────────────────────────────────

    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam("content") String content,
                             Principal principal) {
        Post post = postService.getPostById(id);
        User author = userService.findByUsername(principal.getName());
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setAuthor(author);
        commentService.addComment(comment);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/posts/" + postId;
    }
}