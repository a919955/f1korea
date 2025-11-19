package com.example.f1.repository;

import com.example.f1.entity.Post;
import com.example.f1.entity.Post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    Page<Post> findByPostType(PostType postType, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% OR p.tags LIKE %:keyword%")
    Page<Post> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.postType = :postType AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword% OR p.tags LIKE %:keyword%)")
    Page<Post> findByPostTypeAndKeyword(@Param("postType") PostType postType, @Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.author.username LIKE %:keyword%")
    Page<Post> findByAuthorUsername(@Param("keyword") String keyword, Pageable pageable);
    
    Page<Post> findByIsNoticeTrue(Pageable pageable);
    Page<Post> findByIsHotTrue(Pageable pageable);
}
