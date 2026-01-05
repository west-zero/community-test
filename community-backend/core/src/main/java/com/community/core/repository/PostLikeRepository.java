package com.community.core.repository;

import com.community.core.entity.PostLike;
import com.community.core.entity.Post;
import com.community.core.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndMember(Post post, Member member);
    boolean existsByPostAndMember(Post post, Member member);
    void deleteByPostAndMember(Post post, Member member);
}
