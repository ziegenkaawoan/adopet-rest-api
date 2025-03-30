package com.adopet.adopet_rest_api.repository;

import com.adopet.adopet_rest_api.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}
