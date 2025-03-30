package com.adopet.adopet_rest_api.controller;

import com.adopet.adopet_rest_api.entity.User;
import com.adopet.adopet_rest_api.model.UploadPostRequest;
import com.adopet.adopet_rest_api.model.UploadPostResponse;
import com.adopet.adopet_rest_api.repository.PostRepository;
import com.adopet.adopet_rest_api.repository.UserRepository;
import com.adopet.adopet_rest_api.security.JwtUtil;
import com.adopet.adopet_rest_api.service.PostService;
import com.adopet.adopet_rest_api.service.ValidationService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PostService postService;

    @PostMapping(
            path = "/api/posts"
    )
    public ResponseEntity<?> uploadPost(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("petName") String petName,
            @RequestParam("petBreed") String petBreed,
            @RequestParam("petType") String petType,
            @RequestParam("petAge") int petAge,
            @RequestParam("description") String description,
            @RequestParam("confidenceScore") double confidenceScore,
            @RequestPart("image")MultipartFile imageFile)
    {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT Token is missing");
        }

        String token = authHeader.substring(7);
        if(!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UploadPostRequest request = UploadPostRequest.builder()
                .petName(petName)
                .petBreed(petBreed)
                .petType(petType)
                .petAge(petAge)
                .description(description)
                .isAvailable(true)
                .confidenceScore(confidenceScore)
                .petOwnerId(user.getId())
                .build();

        Long postId = postService.upload(user, request, imageFile);
        UploadPostResponse response = UploadPostResponse.builder()
                .message("Post Successfully created")
                .postId(postId)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
