package com.example.demo.controllers;

import com.example.demo.Post;
import com.example.demo.PostService;
import com.example.demo.User;
import com.example.demo.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    // Afficher tous les articles
    @GetMapping("/posts")
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "list";
    }

    // Afficher un article par son ID
    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "view";
    }

    // Afficher le formulaire de création
    @GetMapping("/posts/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "form";
    }

    // Créer un article — associe l'utilisateur connecté comme auteur
    @PostMapping("/posts")
    public String createPost(@ModelAttribute Post post, Principal principal) {
        User author = userService.findByUsername(principal.getName());
        post.setAuthor(author);
        postService.createPost(post);
        return "redirect:/posts";
    }

    // Afficher le formulaire de modification
    @GetMapping("/posts/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "form";
    }

    // Modifier un article
    @PostMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post, Principal principal) {
        Post existing = postService.getPostById(id);
        existing.setTitle(post.getTitle());
        existing.setContent(post.getContent());
        postService.updatePost(existing);
        return "redirect:/posts";
    }

    // Supprimer un article
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}