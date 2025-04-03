package com.adopet.adopet_rest_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryPost {
    private Long postId;

    private String petName;

    private String petBreed;

    private String petType;

    private String imageUrl;

    private String description;

    private double confidenceScore;

    private Boolean isAvailable;

    private LocalDateTime postDate;
}
