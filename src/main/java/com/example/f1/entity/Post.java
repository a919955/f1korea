package com.example.f1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    
    @NotBlank
    @Size(min = 1, max = 200)
    @Column(nullable = false)
    private String title;
    
    @NotBlank
    @Size(min = 1, max = 5000)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "post_type")
    private PostType postType = PostType.GENERAL;
    
    @Column(name = "ends_at")
    private LocalDateTime endsAt;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "like_count")
    private Integer likeCount = 0;
    
    @Column(name = "comment_count")
    private Integer commentCount = 0;
    
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;
    
    @Column(name = "is_notice")
    private Boolean isNotice = false;
    
    @Column(name = "is_hot")
    private Boolean isHot = false;
    
    @Column(name = "hot_score")
    private Integer hotScore = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Post() {}
    
    public Post(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public void setAuthor(User author) {
        this.author = author;
    }
    
    public LocalDateTime getEndsAt() {
        return endsAt;
    }
    
    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public PostType getPostType() {
        return postType;
    }
    
    public void setPostType(PostType postType) {
        this.postType = postType;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public Boolean getIsNotice() {
        return isNotice;
    }
    
    public void setIsNotice(Boolean isNotice) {
        this.isNotice = isNotice;
    }
    
    public Boolean getIsHot() {
        return isHot;
    }
    
    public void setIsHot(Boolean isHot) {
        this.isHot = isHot;
    }
    
    public Integer getHotScore() {
        return hotScore;
    }
    
    public void setHotScore(Integer hotScore) {
        this.hotScore = hotScore;
    }
    
    public enum PostType {
        GENERAL, NOTICE, INFORMATION, CHAT, REVIEW, QUESTION, VOTE
    }
}