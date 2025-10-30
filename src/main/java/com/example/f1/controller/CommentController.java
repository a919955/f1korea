package com.example.f1.controller;

import com.example.f1.entity.Comment;
import com.example.f1.entity.Post;
import com.example.f1.entity.User;
import com.example.f1.repository.CommentRepository;
import com.example.f1.service.PostService;
import com.example.f1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
        return ResponseEntity.ok(comments);
    }
    
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Post post = postService.getPostById(request.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            
            Comment comment = new Comment();
            comment.setPost(post);
            comment.setUser(user);
            comment.setContent(request.getContent());
            
            Comment savedComment = commentRepository.save(comment);
            
            // 댓글 수 업데이트
            postService.updateCommentCount(request.getPostId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Comment created successfully");
            response.put("comment", savedComment);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Comment not found"));
            
            // 작성자만 삭제 가능
            if (!comment.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("You can only delete your own comments");
            }
            
            commentRepository.delete(comment);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Comment deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Request DTO
    public static class CommentRequest {
        private Long postId;
        private String content;
        
        // Getters and Setters
        public Long getPostId() {
            return postId;
        }
        
        public void setPostId(Long postId) {
            this.postId = postId;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
}

