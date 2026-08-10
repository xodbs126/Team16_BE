package com.kakaotechcampus.team16be.group.domain;

import com.kakaotechcampus.team16be.user.domain.User;

public class GroupFixture {

    public static Group createGroup(User leader) {
        return Group.builder()
                .user(leader)
                .name("스터디 그룹")
                .intro("같이 공부하실 분")
                .capacity(10)
                .build();
    }
}
