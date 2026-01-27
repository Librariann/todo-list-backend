package com.librarian.todo_list.security.jwt;

import com.librarian.todo_list.auth.service.SessionService;
import com.librarian.todo_list.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 * 모든 요청에서 JWT 토큰을 검증하고 인증 정보를 SecurityContext에 설정
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final SessionService sessionService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 요청에서 JWT 토큰 추출
            String jwt = getJwtFromRequest(request);
            
            // 토큰 유효성 검증
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                // 토큰에서 사용자명과 세션 ID 추출
                String username = jwtTokenProvider.getEmailFromToken(jwt);
                String sessionId = jwtTokenProvider.getSessionIdFromToken(jwt);
                
                // 세션 유효성 검증 (세션 ID가 있는 경우)
                if (sessionId != null && !sessionService.isValidSession(sessionId)) {
                    log.debug("유효하지 않은 세션: sessionId={}", sessionId);
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // 사용자 정보 조회
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                
                // 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                log.debug("사용자 인증 성공: {}", username);
            }
        } catch (Exception ex) {
            log.error("SecurityContext에서 사용자 인증을 설정할 수 없습니다", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 요청 헤더에서 JWT 토큰 추출
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
