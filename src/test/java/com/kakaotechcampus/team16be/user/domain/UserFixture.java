package com.kakaotechcampus.team16be.user.domain;

import static org.junit.jupiter.api.Assertions.*;

public class UserFixture {

    public static User createUser(){
        return User.builder()
                .id(1L)
                .kakaoId("TestUserKakaoId")
                .nickname("TestUser")
                .role(Role.USER)
                .verificationStatus(VerificationStatus.UNVERIFIED)
                .build();
    }

    public static User createAdmin(){
        return User.builder()
                .id(2L)
                .kakaoId("AdminKakaoId")
                .nickname("Admin")
                .role(Role.ADMIN)
                .verificationStatus(VerificationStatus.VERIFIED)
                .build();

    }

    public static User createVerifiedUser() {
        return User.builder()
                .id(3L)
                .kakaoId("VerifiedKakaoId")
                .nickname("VerifiedUser")
                .role(Role.USER)
                .verificationStatus(VerificationStatus.VERIFIED)
                .studentIdImageUrl("student-id.png")
                .build();
    }

}
