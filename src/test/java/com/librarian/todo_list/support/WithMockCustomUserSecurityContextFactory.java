package com.librarian.todo_list.support;

import com.librarian.todo_list.security.CustomUserDetails;
import com.librarian.todo_list.user.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        User user = User.builder()
                .nickname(annotation.nickname())
                .email(annotation.email())
                .name(annotation.name())
                .password("encoded_password")
                .role(User.UserRole.valueOf(annotation.role()))
                .status(User.UserStatus.ACTIVE)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
