package com.adopet.adopet_rest_api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {
    @NotBlank
    @Size(max=20)
    private String username;

    @NotBlank
    @Size(max=20)
    private String email;

    @NotBlank
    @Size(max=20)
    private String phoneNumber;

    @NotBlank
    @Size(max=20)
    private String password;
}
