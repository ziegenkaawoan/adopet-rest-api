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
public class FilteredPostResponse {

    private Long postId;

    private String petName;

    private String petBreed;

    private String petType;

    private PetOwnerModel petOwner;

    private String imageUrl;

    private String description;

    private LocalDateTime postDate;

    private Double confidenceScore;

    private boolean isAvailable;

    private int petAge;
}
