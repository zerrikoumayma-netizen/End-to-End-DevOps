package com.example.demo;

import jakarta.persistence.*;

@Entity
// Table "video_post" avec la colonne "url" en plus
public class VideoPost extends Post {

    private String url; // URL de la vidéo (YouTube, etc.)

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}