package com.example.demo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reaction",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
// Un user ne peut avoir qu'une seule réaction par post
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Type de réaction : LIKE, LOVE, HAHA, WOW, SAD, ANGRY
    @Enumerated(EnumType.STRING)
    private ReactionType type;

    private LocalDateTime reactedAt;

    // Côté User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Côté Post
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @PrePersist
    public void prePersist() {
        this.reactedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ReactionType getType() { return type; }
    public void setType(ReactionType type) { this.type = type; }

    public LocalDateTime getReactedAt() { return reactedAt; }
    public void setReactedAt(LocalDateTime reactedAt) { this.reactedAt = reactedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
}