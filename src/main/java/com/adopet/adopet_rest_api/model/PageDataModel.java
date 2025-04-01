package com.adopet.adopet_rest_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDataModel {
    private int currentPage;
    private int totalPages;
    private Long totalPosts;
}
