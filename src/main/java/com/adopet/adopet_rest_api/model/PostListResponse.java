package com.adopet.adopet_rest_api.model;

import com.adopet.adopet_rest_api.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponse {
    List<DetailPostResponse> data;
    PageDataModel page;
}
