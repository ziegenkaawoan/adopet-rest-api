package com.adopet.adopet_rest_api.controller;

import com.adopet.adopet_rest_api.entity.Post;
import com.adopet.adopet_rest_api.entity.User;
import com.adopet.adopet_rest_api.model.DetailPostResponse;
import com.adopet.adopet_rest_api.model.PostBreedResponse;
import com.adopet.adopet_rest_api.model.UploadPostRequest;
import com.adopet.adopet_rest_api.model.UploadPostResponse;
import com.adopet.adopet_rest_api.repository.PostRepository;
import com.adopet.adopet_rest_api.repository.UserRepository;
import com.adopet.adopet_rest_api.security.JwtUtil;
import com.adopet.adopet_rest_api.service.PostService;
import com.adopet.adopet_rest_api.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Slf4j
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

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

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
            @RequestPart("image") MultipartFile imageFile)
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

    @GetMapping(
            path = "/api/posts/{postId}"
    )
    public ResponseEntity<?> getPostDetail(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("postId") Long postId
    ) {
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

        DetailPostResponse response =  postService.getPostDetail(user, postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(
            path = "/api/posts/breed/{petBreed}"
    )
    public ResponseEntity<?> getPostByBreed(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("petBreed") String breed
    ) {
        logger.info("Received request to fetch posts by breed: {}", breed);

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT Token is missing");
        }

        String token = authHeader.substring(7);

        logger.info("Extracted Token: {}", token);

        if(!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }

        List<PostBreedResponse> postsList = postService.getByBreed(breed);

        if(postsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.EMPTY_LIST);
        }
        return ResponseEntity.ok(postsList);
    }

}
