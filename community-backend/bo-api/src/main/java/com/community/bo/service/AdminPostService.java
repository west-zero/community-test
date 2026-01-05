package com.community.bo.service;

import com.community.core.dto.PostResponse;
import com.community.core.entity.Post;
import com.community.core.repository.CommentRepository;
import com.community.core.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::toPostResponse);
    }

    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("Post not found");
        }
        postRepository.deleteById(id);
    }

    private PostResponse toPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorNickname(post.getAuthor().getNickname())
                .authorId(post.getAuthor().getId())
                .menuId(post.getMenu().getId())
                .menuName(post.getMenu().getName())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount((int) commentRepository.countByPost(post))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .liked(false)
                .build();
    }
}
