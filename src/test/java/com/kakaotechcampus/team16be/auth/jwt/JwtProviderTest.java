package com.kakaotechcampus.team16be.auth.jwt;

import com.kakaotechcampus.team16be.auth.exception.JwtException;
import com.kakaotechcampus.team16be.user.domain.User;
import com.kakaotechcampus.team16be.user.domain.UserFixture;
import io.jsonwebtoken.Claims;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.*;


class JwtProviderTest {


    private JwtProvider jwtProvider;
    private static final String TEST_SECRET =
            "test-secret-key-for-jwt-must-be-at-least-32-bytes-long";
    private static final long EXPIRATION_SECONDS = 1800;
    private static final Instant FIXED_NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Instant T0 = Instant.parse(("2026-01-01T00:00:00Z"));

    private JwtProvider providerAt(Instant now) {
        return new JwtProvider(TEST_SECRET,
                1800,
                Clock.fixed(now, ZoneOffset.UTC));
    }

    private JwtProvider jwtProvider() {
        return providerAt(FIXED_NOW);
    }

    @Test
    void 일반_유저_토큰에는_USER_역할이_담긴다() {
        User user = UserFixture.createUser();
        JwtProvider provider = jwtProvider();

        Claims claims = provider.parseToken(provider.createToken(user));

        assertThat(claims.getSubject()).isEqualTo(String.valueOf(user.getId()));
        assertThat(claims.get("kakaoId")).isEqualTo(user.getKakaoId());
        assertThat(claims.get("role")).isEqualTo("USER");
    }

    @Test
    void 관리자_토큰에는_ADMIN_역할이_담긴다() {
        User admin = UserFixture.createAdmin();
        JwtProvider provider = jwtProvider();

        Claims claims = provider.parseToken(provider.createToken(admin));

        assertThat(claims.get("role")).isEqualTo("ADMIN");
    }

    @Test
    void 만료시각은_발급시각에_유효기간을_더한_값이다() {
        JwtProvider provider = providerAt(FIXED_NOW);

        Claims claims = provider.parseToken(provider.createToken(UserFixture.createUser()));

        assertThat(claims.getIssuedAt().toInstant()).isEqualTo(FIXED_NOW);
        assertThat(claims.getExpiration().toInstant())
                .isEqualTo(FIXED_NOW.plusSeconds(EXPIRATION_SECONDS));
    }

    @Test
    void 유효기간이_지난_토큰은_거부된다() {
        String token = providerAt(T0).createToken(UserFixture.createUser());

        JwtProvider afterExpiry = providerAt(T0.plusSeconds(EXPIRATION_SECONDS + 1));

        assertThatThrownBy(() -> afterExpiry.parseToken(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void 유효기간이_남은_토큰은_통과한다() {
        String token = providerAt(T0).createToken(UserFixture.createUser());

        JwtProvider afterExpiry = providerAt(T0.plusSeconds(EXPIRATION_SECONDS - 1));

        assertThatCode(() -> afterExpiry.parseToken(token))
                .doesNotThrowAnyException();
    }

    @Test
    void 서명이_훼손된_토큰은_거부된다() {
        JwtProvider provider = jwtProvider();
        String token = provider.createToken(UserFixture.createUser()) + "abc";

        assertThatThrownBy(() -> provider.parseToken(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void JWT_형식이_아닌_문자열은_거부된다() {
        assertThatThrownBy(() -> jwtProvider().parseToken("아무말123123"))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void 다른_secret으로_서명된_토큰은_거부된다() {
        JwtProvider attacker = new JwtProvider(
                "attacker-secret-key-must-also-be-at-least-32-bytes",
                EXPIRATION_SECONDS, Clock.fixed(FIXED_NOW, ZoneOffset.UTC));
        String forged = attacker.createToken(UserFixture.createAdmin());

        assertThatThrownBy(() -> jwtProvider().parseToken(forged))
                .isInstanceOf(JwtException.class);
    }
}