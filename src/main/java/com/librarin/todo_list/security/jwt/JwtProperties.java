package com.librarin.todo_list.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 Properties
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    
    /**
     * JWT 서명에 사용할 비밀키
     */
    private String secret;
    
    /**
     * Access Token 만료 시간 (밀리초)
     */
    private Long expiration;
    
    /**
     * Refresh Token 만료 시간 (밀리초)
     */
    private Long refreshExpiration;
    
    /**
     * 쿠키 보안 설정 (HTTPS 전용)
     */
    private Boolean cookieSecure;
    
    /**
     * 쿠키 SameSite 설정
     */
    private String cookieSameSite;
}
