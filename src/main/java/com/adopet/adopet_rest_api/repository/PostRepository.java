package com.adopet.adopet_rest_api.repository;

import com.adopet.adopet_rest_api.entity.Post;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNullApi;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE LOWER(p.petBreed) LIKE LOWER(CONCAT('%', :petBreed, '%'))")
    List<Post> searchByPetBreed(@Param("petBreed") String petBreed);

    @Query("SELECT p FROM Post p WHERE LOWER(p.petType) = LOWER(:petType)")
    Page<Post> searchByPetType(@Param("petType") String petType);

    Page<Post> findByPetTypeAndPetBreedAndIsAvailable(
            String petType,
            String petBreed,
            Boolean isAvailable,
            Pageable pageable
    );

    Page<Post> findByPetOwnerIdAndIsAvailable(Long petOwnerId,
                                              Boolean isAvailable,
                                              Pageable pageable
    );
}
