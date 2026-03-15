package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "post_view",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
// uniqueConstraint : un utilisateur ne peut avoir qu'une seule vue par post
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}