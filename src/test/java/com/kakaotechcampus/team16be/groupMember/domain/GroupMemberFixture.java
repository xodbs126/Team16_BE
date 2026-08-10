package com.kakaotechcampus.team16be.groupMember.domain;

import com.kakaotechcampus.team16be.group.domain.Group;
import com.kakaotechcampus.team16be.user.domain.User;

public class GroupMemberFixture {

    public static GroupMember createActiveMember(Group group, User user) {
        return GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupRole.MEMBER)
                .status(GroupMemberStatus.ACTIVE)
                .build();
    }

    public static GroupMember createInactiveMember(Group group, User user) {
        return GroupMember.builder()
                .group(group)
                .user(user)
                .role(GroupRole.MEMBER)
                .status(GroupMemberStatus.LEFT)
                .build();
    }
}