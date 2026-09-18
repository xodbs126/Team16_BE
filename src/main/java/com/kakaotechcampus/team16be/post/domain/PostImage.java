package com.kakaotechcampus.team16be.post.domain;


import jakarta.persistence.*;
import lombok.Builder;

@Entity
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String imgUrl;

    private int sortOrder;

    @Builder
    public PostImage(String imgUrl, int sortOrder, Post post) {
        this.imgUrl = imgUrl;
        this.sortOrder = sortOrder;
        this.post = post;
    }

}
