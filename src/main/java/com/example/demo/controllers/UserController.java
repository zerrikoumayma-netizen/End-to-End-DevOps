package com.example.demo.controllers;

import com.example.demo.Profile;
import com.example.demo.User;
import com.example.demo.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // AJOUTÉ : sans ce mapping Spring Security boucle en redirect infini
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           @RequestParam(required = false) String bio,
                           @RequestParam(required = false) String avatar) {
        if ((bio != null && !bio.isBlank()) || (avatar != null && !avatar.isBlank())) {
            Profile profile = new Profile();
            profile.setBio(bio);
            profile.setAvatar(avatar);
            profile.setUser(user);
            user.setProfile(profile);
        }
        userService.createUser(user);
        return "redirect:/login";
    }

}