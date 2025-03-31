package com.adopet.adopet_rest_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetOwnerModel {
    private Long id;

    private String username;

    private String email;

    private String phoneNumber;

}
