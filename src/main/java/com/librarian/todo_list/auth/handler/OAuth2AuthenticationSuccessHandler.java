package com.librarian.todo_list.auth.handler;

import com.librarian.todo_list.auth.oauth2.CustomOAuth2User;
import com.librarian.todo_list.auth.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.librarian.todo_list.auth.service.SessionService;
import com.librarian.todo_list.security.jwt.JwtTokenProvider;
import com.librarian.todo_list.user.entity.User;
import com.librarian.todo_list.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieRepository;

    @Value("${app.oauth2.redirect-uri:http://localhost:3000/oauth/callback}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        User user = userRepository.findByEmail(oAuth2User.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + oAuth2User.getEmail()));

        // 기존 AuthService.login()과 동일한 JWT 발급 흐름
        String sessionId = sessionService.createSession(user);
        String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), sessionId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail(), sessionId);

        log.info("OAuth2 로그인 성공: email={}, provider={}", user.getEmail(), oAuth2User.getProvider());

        // 쿠키 정리
        cookieRepository.removeAuthorizationRequest(request, response);

        // 프론트엔드로 리다이렉트 (토큰을 쿼리 파라미터로 전달)
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", accessToken)
                .queryParam("refresh", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
