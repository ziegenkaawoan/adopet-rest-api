package com.adopet.adopet_rest_api.model;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadPostRequest {

    @NotBlank
    @Size(max = 20)
    private String petName;

    @NotBlank
    @Size(max = 20)
    private String petBreed;

    @NotBlank
    @Size(max = 10)
    private String petType;

    @NotNull
    private Long petOwnerId;

    @NotBlank
    private String imageUrl;

    @NotBlank
    @Size(max = 255)
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private Double confidenceScore;

    @NotNull
    private Boolean isAvailable;

    @Min(0)
    @NotNull
    private Integer petAge;
}
