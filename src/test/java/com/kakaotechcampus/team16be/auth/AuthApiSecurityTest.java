package com.kakaotechcampus.team16be.auth;


import com.kakaotechcampus.team16be.auth.controller.AdminAuthController;
import com.kakaotechcampus.team16be.auth.controller.AuthController;
import com.kakaotechcampus.team16be.auth.dto.KakaoLoginResponse;
import com.kakaotechcampus.team16be.auth.jwt.JwtProvider;
import com.kakaotechcampus.team16be.auth.service.KakaoAuthService;
import com.kakaotechcampus.team16be.common.config.Webconfig;
import com.kakaotechcampus.team16be.common.exception.GlobalExceptionHandler;
import com.kakaotechcampus.team16be.common.interceptor.AdminCheckInterceptor;
import com.kakaotechcampus.team16be.common.interceptor.LoginCheckInterceptor;
import com.kakaotechcampus.team16be.common.resolver.LoginUserArgumentResolver;
import com.kakaotechcampus.team16be.user.domain.User;
import com.kakaotechcampus.team16be.user.domain.UserFixture;
import com.kakaotechcampus.team16be.user.repository.UserRepository;
import com.kakaotechcampus.team16be.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {AuthController.class, AdminAuthController.class})
@Import({Webconfig.class,
        LoginCheckInterceptor.class,
        AdminCheckInterceptor.class,
        LoginUserArgumentResolver.class,
        GlobalExceptionHandler.class,
        JwtProvider.class
})

public class AuthApiSecurityTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    JwtProvider jwtProvider;

    @MockitoBean
    UserRepository userRepository;
    @MockitoBean
    KakaoAuthService kakaoAuthService;
    @MockitoBean
    UserService userService;

    @TestConfiguration
    static class TestClockConfig {
        @Bean Clock clock(){
            return Clock.systemDefaultZone();
        }
    }

    @Test
    void 토큰_없이_보호된_API를_호출하면_401() throws Exception{
        mockMvc.perform(get("/api/admin/auth/student-verification"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void 일반유저가_admin_API를_호출하면_401() throws Exception {
        String token = jwtProvider.createToken(UserFixture.createUser());
        mockMvc.perform(get("/api/admin/auth/student-verification")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void 관리자는_admin_API에_접근할_수_있다() throws Exception {
        User admin = UserFixture.createAdmin();
        given(userRepository.findById(admin.getId())).willReturn(Optional.of(admin));
        given(userService.getStudentIdImageUrl(any())).willReturn("https://example.com/img.png");

        mockMvc.perform(get("/api/admin/auth/student-verification")
                        .header("Authorization", "Bearer " + jwtProvider.createToken(admin)))
                .andExpect(status().isOk());
    }

    @Test
    void 로그인은_토큰_없이_호출할_수_있다() throws Exception {
        given(kakaoAuthService.loginWithCode(any(), any()))
                .willReturn(new KakaoLoginResponse("dummy"));

        mockMvc.perform(get("/api/auth/kakao-login").param("code", "test-code"))
                .andExpect(status().isOk());
    }

    @Test
    void Bearer만_있고_토큰이_없으면_401() throws Exception {
        mockMvc.perform(get("/api/auth/student-verification/status")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized());
    }
}
