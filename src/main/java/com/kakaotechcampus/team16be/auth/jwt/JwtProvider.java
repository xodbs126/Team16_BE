package com.kakaotechcampus.team16be.auth.jwt;

import com.kakaotechcampus.team16be.auth.exception.JwtErrorCode;
import com.kakaotechcampus.team16be.auth.exception.JwtException;
import com.kakaotechcampus.team16be.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationSeconds;
    private final java.time.Clock clock; // 발급측 시간
    private final io.jsonwebtoken.Clock jwtClock; //검증 측 시간

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-seconds}") long accessTokenExpirationSeconds,
           java.time.Clock clock) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
        this.clock = clock;
        this.jwtClock = ()->Date.from(clock.instant());

    }
    /**
     * JWT 토큰 생성
     * @param user 토큰에 담을 User 정보
     * @return JWT 문자열
     */
    public String createToken(User user) {
        Instant now = clock.instant();
        Instant expiryDate = now.plusSeconds(accessTokenExpirationSeconds);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("kakaoId", user.getKakaoId())
                .claim("role", user.getRole().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 파싱 및 검증
     * @param token 클라이언트가 전달한 JWT
     * @return Claims payload
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .setClock(jwtClock)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtException(JwtErrorCode.EXPIRED_TOKEN);
        } catch (SignatureException e) {
            throw new JwtException(JwtErrorCode.INVALID_SIGNATURE);
        } catch (MalformedJwtException |UnsupportedJwtException | IllegalArgumentException e) {
            throw new JwtException(JwtErrorCode.WRONG_HEADER_TOKEN);
        }
    }

    /**
     * JWT에서 userId 추출
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        try {
            return Long.parseLong(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new JwtException(JwtErrorCode.WRONG_HEADER_TOKEN);
        }
    }

    /**
     * JWT에서 kakaoId 추출
     */
    public String getKakaoId(String token) {
        Claims claims = parseToken(token);
        Object kakaoId = claims.get("kakaoId");
        return kakaoId != null ? kakaoId.toString() : null;
    }
}
