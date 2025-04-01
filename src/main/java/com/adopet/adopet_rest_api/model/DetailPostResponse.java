package com.adopet.adopet_rest_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailPostResponse {

    private Long postId;

    private String petName;

    private String petBreed;

    private String petType;

    private PetOwnerModel petOwner;

    private String imageUrl;

    private double confidenceScore;

    private String description;

    private boolean isAvailable;

    private int petAge;

    private LocalDateTime postDate;

}
