package com.example.demo.controllers;
import com.example.demo.Post;
import com.example.demo.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
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
        model.addAttribute("post", new Post()); // ← "post" doit matcher th:object="${post}"
        return "form";
    }

    // Créer un article
    @PostMapping("/posts")
    public String createPost(Post post) {
        postService.createPost(post);
        return "redirect:/posts";
    }

    // Afficher le formulaire de modification
    @GetMapping("/posts/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "posts/form";
    }

    // Supprimer un article
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}