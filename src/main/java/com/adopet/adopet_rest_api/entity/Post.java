package com.adopet.adopet_rest_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(name="pet_name")
    private String petName;

    @Column(name="pet_breed")
    private String petBreed;

    @Column(name = "pet_type")
    private String petType;

    @ManyToOne
    @JoinColumn(
            name = "pet_owner_id",
            referencedColumnName = "id"
    )
    private User petOwner;

    @Column(name = "image_url")
    private String imageUrl;

    private String description;

    @Column(name = "post_date")
    private LocalDateTime postDate;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "is_available", columnDefinition = "BIT(1)")
    private boolean available;

    @Column(name = "pet_age")
    private int petAge;

}
