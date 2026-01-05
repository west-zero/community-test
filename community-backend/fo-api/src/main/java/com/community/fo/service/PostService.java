package com.community.fo.service;

import com.community.core.dto.PostRequest;
import com.community.core.dto.PostResponse;
import com.community.core.entity.Member;
import com.community.core.entity.Menu;
import com.community.core.entity.Post;
import com.community.core.entity.PostLike;
import com.community.core.repository.CommentRepository;
import com.community.core.repository.PostLikeRepository;
import com.community.core.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final MenuService menuService;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public Page<PostResponse> getPosts(Long menuId, Pageable pageable, Long currentMemberId) {
        Page<Post> posts;
        if (menuId != null) {
            Menu menu = menuService.getMenuById(menuId);
            posts = postRepository.findByMenuOrderByCreatedAtDesc(menu, pageable);
        } else {
            posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return posts.map(post -> toPostResponse(post, currentMemberId));
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getMyPosts(Long memberId, Pageable pageable) {
        Member member = authService.getMemberById(memberId);
        Page<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(member, pageable);
        return posts.map(post -> toPostResponse(post, memberId));
    }

    @Transactional
    public PostResponse getPostById(Long id, Long currentMemberId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        postRepository.incrementViewCount(id);
        post.setViewCount(post.getViewCount() + 1);
        return toPostResponse(post, currentMemberId);
    }

    @Transactional
    public PostResponse createPost(PostRequest request, Long memberId) {
        Member author = authService.getMemberById(memberId);
        Menu menu = menuService.getMenuById(request.getMenuId());

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .thumbnailUrl(request.getThumbnailUrl())
                .author(author)
                .menu(menu)
                .viewCount(0)
                .likeCount(0)
                .build();

        post = postRepository.save(post);
        return toPostResponse(post, memberId);
    }

    @Transactional
    public PostResponse updatePost(Long id, PostRequest request, Long memberId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getAuthor().getId().equals(memberId)) {
            throw new IllegalArgumentException("You can only edit your own posts");
        }

        Menu menu = menuService.getMenuById(request.getMenuId());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setThumbnailUrl(request.getThumbnailUrl());
        post.setMenu(menu);

        post = postRepository.save(post);
        return toPostResponse(post, memberId);
    }

    @Transactional
    public void deletePost(Long id, Long memberId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getAuthor().getId().equals(memberId)) {
            throw new IllegalArgumentException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }

    @Transactional
    public boolean toggleLike(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        Member member = authService.getMemberById(memberId);

        if (postLikeRepository.existsByPostAndMember(post, member)) {
            postLikeRepository.deleteByPostAndMember(post, member);
            postRepository.decrementLikeCount(postId);
            return false;
        } else {
            PostLike like = PostLike.builder()
                    .post(post)
                    .member(member)
                    .build();
            postLikeRepository.save(like);
            postRepository.incrementLikeCount(postId);
            return true;
        }
    }

    private PostResponse toPostResponse(Post post, Long currentMemberId) {
        boolean liked = false;
        if (currentMemberId != null) {
            Member member = authService.getMemberById(currentMemberId);
            liked = postLikeRepository.existsByPostAndMember(post, member);
        }

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .thumbnailUrl(post.getThumbnailUrl())
                .authorNickname(post.getAuthor().getNickname())
                .authorId(post.getAuthor().getId())
                .menuId(post.getMenu().getId())
                .menuName(post.getMenu().getName())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount((int) commentRepository.countByPost(post))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .liked(liked)
                .build();
    }
}
