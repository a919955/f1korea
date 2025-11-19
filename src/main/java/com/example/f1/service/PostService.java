package com.example.f1.service;

import com.example.f1.entity.Post;
import com.example.f1.entity.Post.PostType;
import com.example.f1.entity.User;
import com.example.f1.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
    
    public Page<Post> getPostsByType(PostType postType, Pageable pageable) {
        return postRepository.findByPostType(postType, pageable);
    }
    
    public Page<Post> searchPosts(String keyword, Pageable pageable) {
        return postRepository.findByKeyword(keyword, pageable);
    }
    
    public Page<Post> searchPostsByType(PostType postType, String keyword, Pageable pageable) {
        return postRepository.findByPostTypeAndKeyword(postType, keyword, pageable);
    }
    
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }
    
    public Post createPost(String title, String content, User author, PostType postType, String tags) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        post.setPostType(postType);
        post.setTags(tags);
        
        return postRepository.save(post);
    }
    
    public Post updatePost(Long id, String title, String content, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // 작성자만 수정 가능
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only edit your own posts");
        }
        
        post.setTitle(title);
        post.setContent(content);
        
        return postRepository.save(post);
    }
    
    public void deletePost(Long id, User currentUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // 작성자만 삭제 가능
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own posts");
        }
        
        postRepository.delete(post);
    }
    
    public Post incrementViewCount(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.setViewCount(post.getViewCount() + 1);
        return postRepository.save(post);
    }
    
    public Post toggleLike(Long id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // TODO: 좋아요 중복 체크 로직 추가 필요
        post.setLikeCount(post.getLikeCount() + 1);
        
        // HOT 점수 계산 (조회수 + 좋아요수 + 댓글수)
        int hotScore = post.getViewCount() + post.getLikeCount() + post.getCommentCount();
        post.setHotScore(hotScore);
        
        if (hotScore > 100) {
            post.setIsHot(true);
        }
        
        return postRepository.save(post);
    }
    
    public void updateCommentCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.setCommentCount(post.getCommentCount() + 1);
        
        // HOT 점수 재계산
        int hotScore = post.getViewCount() + post.getLikeCount() + post.getCommentCount();
        post.setHotScore(hotScore);
        
        if (hotScore > 100) {
            post.setIsHot(true);
        }
        
        postRepository.save(post);
    }
    
    public Page<Post> getNotices(Pageable pageable) {
        return postRepository.findByIsNoticeTrue(pageable);
    }
    
    public Page<Post> getHotPosts(Pageable pageable) {
        return postRepository.findByIsHotTrue(pageable);
    }
}