package com.kakaotechcampus.team16be.post.service;

import com.kakaotechcampus.team16be.group.domain.Group;
import com.kakaotechcampus.team16be.group.domain.GroupFixture;
import com.kakaotechcampus.team16be.group.service.GroupService;
import com.kakaotechcampus.team16be.groupMember.domain.GroupMember;
import com.kakaotechcampus.team16be.groupMember.domain.GroupMemberFixture;
import com.kakaotechcampus.team16be.groupMember.service.GroupMemberService;
import com.kakaotechcampus.team16be.post.domain.Post;
import com.kakaotechcampus.team16be.post.dto.CreatePostRequest;
import com.kakaotechcampus.team16be.post.repository.PostRepository;
import com.kakaotechcampus.team16be.user.domain.User;
import com.kakaotechcampus.team16be.user.domain.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)   // ← 스프링 컨텍스트 안 띄움, Mockito만 초기화
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private GroupService groupService;

    @Mock
    private GroupMemberService groupMemberService;

    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(postRepository, groupService, groupMemberService);
    }

    @Test
    void createPost() {

        User user = UserFixture.createUser();
        Group group = GroupFixture.createGroup(user);
        GroupMember activeMember = GroupMemberFixture.createActiveMember(group, user);
        CreatePostRequest request = new CreatePostRequest(
                1L, "테스트 제목", "테스트 내용", List.of("https://image.com/1.png")
        );

        given(groupService.findGroupById(1L)).willReturn(group);
        given(groupMemberService.findByGroupAndUser(group, user)).willReturn(activeMember);
        given(postRepository.save(any(Post.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        Post result = postService.createPost(user, request);

        // then
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertThat(savedPost.getAuthor()).isEqualTo("TestUser");
        assertThat(savedPost.getGroup()).isEqualTo(group);
        assertThat(savedPost.getTitle()).isEqualTo("테스트 제목");
        assertThat(savedPost.getContent()).isEqualTo("테스트 내용");
        assertThat(result).isEqualTo(savedPost);
    }

    @Test
    void deletePost() {
    }

    @Test
    void findByAuthorAndId() {
    }

    @Test
    void updatePost() {
    }

    @Test
    void findById() {
    }
}