package com.adopet.adopet_rest_api.controller;

import com.adopet.adopet_rest_api.entity.User;
import com.adopet.adopet_rest_api.model.*;
import com.adopet.adopet_rest_api.repository.PostRepository;
import com.adopet.adopet_rest_api.repository.UserRepository;
import com.adopet.adopet_rest_api.security.JwtUtil;
import com.adopet.adopet_rest_api.service.PostService;
import com.adopet.adopet_rest_api.service.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
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

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT Token is missing");
        }

        String token = authHeader.substring(7);

        if(!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }

        List<FilteredPostResponse> postsList = postService.getByBreed(breed);

        if(postsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.EMPTY_LIST);
        }
        return ResponseEntity.ok(postsList);
    }

    @GetMapping(
            path = "/api/posts/type/{petType}"
    )
    public ResponseEntity<?> getPostByType(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("petType") String petType
    ) {

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT Token is missing");
        }

        String token = authHeader.substring(7);

        if(!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }

        List<FilteredPostResponse> postsList = postService.getByType(petType);

        if(postsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.EMPTY_LIST);
        }
        return ResponseEntity.ok(postsList);
    }

    @PatchMapping(
            path = "api/posts/{postId}/availability"
    )
    public ResponseEntity<?> updateAvailability(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("postId") Long postId,
            @RequestBody ChangeAvailabilityRequest request
    ) {

        validationService.validate(request);

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT Token is missing");
        }

        String token = authHeader.substring(7);

        if(!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }

        var availability = request.getIsAvailable();

        postService.changeAvailability(postId, availability);
        return ResponseEntity.ok("Success to update post availability");
    }

    @GetMapping(
            path = "api/posts"
    )
    public ResponseEntity<?> getPosts(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "petType", required = false, defaultValue = "Cat") String petType,
            @RequestParam(value = "petBreed", required = false) String petBreed,
            @RequestParam(value = "isAvailable", defaultValue = "true") boolean isAvailable
    ) {

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT Token is missing");
        }

        String token = authHeader.substring(7);

        if(!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        }

        PostListResponse postListResponse = postService.getPosts(petType, petBreed,isAvailable, page, size);

        return ResponseEntity.ok().body(postListResponse);
    }

    @GetMapping(
            path = "api/posts/history"
    )
    public ResponseEntity<?> getUploadHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "isAvailable", defaultValue = "false") Boolean isAvailable
    ) {

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT Token");
        }

        String token = authHeader.substring(7);

        if(!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT Token");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        HistoryListResponse historyListResponse =  postService.getUploadHistory(user, isAvailable, page, size);
        return ResponseEntity.ok(historyListResponse);
    }

    @GetMapping(
            path = "api/posts/history/{postId}"
    )
    public ResponseEntity<?> getHistoryDetail(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("postId") Long postId
    ) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT Token");
        }

        String token = authHeader.substring(7);

        if(!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Invalid JWT token");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        DetailPostResponse detailPostResponse = postService.getPostDetail(user, postId);
        return ResponseEntity.ok(detailPostResponse);
    }

    @GetMapping(
            path = "/api/posts/{filename:..+"
    )
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        try {
            Resource resource = postService.loadFile(fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch(ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(null);
        }
    }
}
