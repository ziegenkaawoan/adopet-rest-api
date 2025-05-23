package com.adopet.adopet_rest_api.service;

import com.adopet.adopet_rest_api.entity.Post;
import com.adopet.adopet_rest_api.entity.User;
import com.adopet.adopet_rest_api.model.*;
import com.adopet.adopet_rest_api.repository.PostRepository;
import com.adopet.adopet_rest_api.repository.UserRepository;
import com.adopet.adopet_rest_api.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UserRepository userRepository;

    // Upload
    @Transactional
    public Long upload(User user, UploadPostRequest request, MultipartFile file) {

        Post newPost = Post.builder()
                .petName(request.getPetName())
                .petType(request.getPetType())
                .petBreed(request.getPetBreed())
                .petOwner(user)
                .postDate(LocalDateTime.now())
                .isAvailable(request.getIsAvailable())
                .confidenceScore(request.getConfidenceScore())
                .description(request.getDescription())
                .build();

        if(file != null && !file.isEmpty()) {
            String imageUrl = saveUploadFile(file);
            newPost.setImageUrl(imageUrl);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is required");
        }
        postRepository.save(newPost);
        return newPost.getPostId();
    }


    // Get All Post
    public PostListResponse getPosts(
            String petType,
            String petBreed,
            Boolean isAvailable,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> pagination;
        if(petType == null || petBreed == null) {
            pagination = postRepository.findAll(pageable);
        } else if(petType.equals("Cat") || petType.equals("Dog")) {
            pagination = postRepository.searchByPetType(petType, pageable);
        }
        else {
            pagination = postRepository.findByPetTypeAndPetBreedAndIsAvailable(petType, petBreed, isAvailable, pageable);
        }
        if(!pagination.isEmpty()) {
           return convertToDetailListResponse(pagination);
        } else {
            return PostListResponse.builder()
                    .data(Collections.emptyList())
                    .page(PageDataModel.builder()
                            .totalPosts(0L)
                            .totalPages(0)
                            .currentPage(0)
                            .build())
                    .build();
        }
    }

    // Get Detail
    public DetailPostResponse getPostDetail(User user, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        PetOwnerModel petOwner = PetOwnerModel.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .id(user.getId())
                .build();

        return DetailPostResponse.builder()
                .petOwner(petOwner)
                .postDate(post.getPostDate())
                .imageUrl(post.getImageUrl())
                .isAvailable(post.getIsAvailable())
                .description(post.getDescription())
                .petName(post.getPetName())
                .petBreed(post.getPetBreed())
                .petType(post.getPetType())
                .postId(post.getPostId())
                .build();
    }

    // Get by Breed
    public List<FilteredPostResponse> getByBreed(String breed) {

        List<Post> postsList = postRepository.searchByPetBreed(breed);
        List<FilteredPostResponse> newList = new ArrayList<>();

        if(!postsList.isEmpty()) {
            for(Post post : postsList) {
                FilteredPostResponse postBreed = FilteredPostResponse.builder()
                        .postId(post.getPostId())
                        .postDate(post.getPostDate())
                        .imageUrl(post.getImageUrl())
                        .petAge(post.getPetAge())
                        .petType(post.getPetType())
                        .confidenceScore(post.getConfidenceScore())
                        .isAvailable(post.getIsAvailable())
                        .description(post.getDescription())
                        .build();

                PetOwnerModel petOwner = PetOwnerModel.builder()
                        .id(post.getPetOwner().getId())
                        .phoneNumber(post.getPetOwner().getPhoneNumber())
                        .email(post.getPetOwner().getEmail())
                        .build();

                postBreed.setPetOwner(petOwner);
                newList.add(postBreed);
            }
        }
        return newList;
    }

    // Change Status
    @Transactional
    public void changeAvailability(Long postId, boolean isAvailable) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to update post availability"));

        post.setIsAvailable(isAvailable);
        postRepository.save(post);
    }

    // Get Upload History
    public HistoryListResponse getUploadHistory(User user, Boolean isAvailable, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Post> pagination = postRepository.findByPetOwnerIdAndIsAvailable(user.getId(), isAvailable, pageable);
        HistoryListResponse historyListResponse;

        if(!pagination.isEmpty()) {
            List<HistoryPost> listHistoryPost = pagination.getContent().stream().map(post ->
                    HistoryPost.builder()
                            .postId(post.getPostId())
                            .description(post.getDescription())
                            .petType(post.getPetType())
                            .confidenceScore(post.getConfidenceScore())
                            .imageUrl(post.getImageUrl())
                            .isAvailable(post.getIsAvailable())
                            .build()
                    ).toList();
            historyListResponse = HistoryListResponse.builder()
                    .posts(listHistoryPost)
                    .page(PageDataModel.builder()
                            .totalPosts(pagination.getTotalElements())
                            .totalPages(pagination.getTotalPages())
                            .currentPage(pagination.getNumber())
                            .build())
                    .build();
        } else {
            historyListResponse = HistoryListResponse.builder()
                    .page(PageDataModel.builder()
                            .totalPosts(0L)
                            .totalPages(0)
                            .currentPage(0)
                            .build())
                    .posts(Collections.emptyList())
                    .build();
        }
        return historyListResponse;

    }


    private String saveUploadFile(MultipartFile file) {
        try {
            String uploadDir = "D:\\adopetFile";
            File directory = new File(uploadDir);
            if(!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File uploadedFile = new File(directory, fileName);

            try (OutputStream os = new FileOutputStream(uploadedFile)) {
                os.write(file.getBytes());
            }
            return "D:/adopetFile/" + fileName;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save file", e);
        }
    }

    public Resource loadFile(String filename) {
        final Path rootLocation = Paths.get("D:/adopetFile");
        try {
            Path file = rootLocation.resolve(   filename).normalize().toAbsolutePath();

            if (!file.startsWith(rootLocation.toAbsolutePath())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: " + filename);
            }

            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not read file: " + filename, e);
        }
    }


    private PostListResponse convertToDetailListResponse(Page<Post> pagination) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/images/";

        List<DetailPostResponse> listPost = pagination.getContent().stream().map(post -> {
            String filename = Paths.get(post.getImageUrl()).getFileName().toString();

            return DetailPostResponse.builder()
                    .postId(post.getPostId())
                    .petName(post.getPetName())
                    .petBreed(post.getPetBreed())
                    .petType(post.getPetType())
                    .imageUrl(baseUrl + filename)
                    .description(post.getDescription())
                    .postDate(post.getPostDate())
                    .confidenceScore(post.getConfidenceScore())
                    .isAvailable(post.getIsAvailable())
                    .petAge(post.getPetAge())
                    .petOwner(new PetOwnerModel(
                            post.getPetOwner().getId(),
                            post.getPetOwner().getUsername(),
                            post.getPetOwner().getEmail(),
                            post.getPetOwner().getPhoneNumber()
                    ))
                    .build();
        }).toList();

        return PostListResponse.builder()
                .data(listPost)
                .page(PageDataModel.builder()
                        .totalPosts(pagination.getTotalElements())
                        .totalPages(pagination.getTotalPages())
                        .currentPage(pagination.getNumber())
                        .build())
                .build();
    }

}