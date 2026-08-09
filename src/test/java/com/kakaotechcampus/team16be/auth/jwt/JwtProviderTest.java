package com.kakaotechcampus.team16be.auth.jwt;

import com.kakaotechcampus.team16be.auth.exception.JwtException;
import com.kakaotechcampus.team16be.user.domain.User;
import com.kakaotechcampus.team16be.user.domain.UserFixture;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;


class JwtProviderTest {


    /***
     * Test 코드에서는 다른 의존성을 건드리지 않도록 도메인 그대로 사용
     */
    private JwtProvider jwtProvider;
    private static final String TEST_SECRET =
            "test-secret-key-for-jwt-must-be-at-least-32-bytes-long";

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "secret", TEST_SECRET);
        jwtProvider.init();
    }

    @Test
    void createToken_User() {
        User testUser = UserFixture.createUser();

        Claims claims = jwtProvider.parseToken(jwtProvider.createToken(testUser));

        assertThat(claims.getSubject()).isEqualTo(String.valueOf(testUser.getId()));
        assertThat(claims.get("kakaoId")).isEqualTo(testUser.getKakaoId());
        assertThat(claims.get("role")).isEqualTo("USER");
    }

    @Test
    void createToken_ADMIN() {

        User testUser = UserFixture.createAdmin();

        Claims claims = jwtProvider.parseToken(jwtProvider.createToken(testUser));

        assertThat(claims.getSubject()).isEqualTo(String.valueOf(testUser.getId()));
        assertThat(claims.get("kakaoId")).isEqualTo(testUser.getKakaoId());
        assertThat(claims.get("role")).isEqualTo("ADMIN");
    }


    @Test
    void parseToken_tampered() {
        String token = jwtProvider.createToken(UserFixture.createUser()) + "abc";

        assertThatThrownBy(() -> jwtProvider.parseToken(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void parseToken_malformed() {
        assertThatThrownBy(() -> jwtProvider.parseToken("아무말123123"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void getUserId() {
        User testUser = UserFixture.createUser();
        String token = jwtProvider.createToken(testUser);
        System.out.println(token);
        assertThat(jwtProvider.getUserId(token)).isEqualTo(testUser.getId());

    }

    @Test
    void getKakaoId() {
        User testUser = UserFixture.createUser();
        String token = jwtProvider.createToken(testUser);
        assertThat(jwtProvider.getKakaoId(token)).isEqualTo(testUser.getKakaoId());
    }
}