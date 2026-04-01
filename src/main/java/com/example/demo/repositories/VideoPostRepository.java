package com.example.demo.repositories;

import com.example.demo.VideoPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoPostRepository extends JpaRepository<VideoPost, Long> {
}