package com.librarian.todo_list.auth.oauth2;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * OAuth2 로그인 성공 시 SecurityContext에 저장되는 Principal 객체
 * SuccessHandler에서 email, provider를 꺼내 JWT 발급에 사용
 */
@Getter
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User delegate;
    private final String email;
    private final String provider;

    public CustomOAuth2User(OAuth2User delegate, String email, String provider) {
        this.delegate = delegate;
        this.email = email;
        this.provider = provider;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }
}
