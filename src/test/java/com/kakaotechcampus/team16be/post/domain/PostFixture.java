package com.kakaotechcampus.team16be.post.domain;

import com.kakaotechcampus.team16be.group.domain.Group;
import com.kakaotechcampus.team16be.post.domain.Post;
import com.kakaotechcampus.team16be.user.domain.User;

import java.util.List;

public class PostFixture {
    public static Post createPost(User user, Group group, String title, String content, List<String> imageUrls) {
        return Post.builder()
                .author(user.getNickname())
                .group(group)
                .title(title)
                .content(content)
                .imageUrls(imageUrls)
                .build();
    }
}
