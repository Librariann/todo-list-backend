package com.librarian.todo_list.auth.oauth2;

import com.librarian.todo_list.user.entity.User;
import com.librarian.todo_list.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId.toLowerCase(), oAuth2User.getAttributes());

        if (userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_not_found"), "OAuth2 계정에서 이메일을 가져올 수 없습니다");
        }

        User user = findOrCreateUser(userInfo, registrationId);
        return new CustomOAuth2User(oAuth2User, user.getEmail(), registrationId);
    }

    private User findOrCreateUser(OAuth2UserInfo userInfo, String provider) {
        // 1) 같은 provider + providerId로 이미 가입된 유저
        User user = userRepository.findByProviderAndProviderId(provider, userInfo.getProviderId())
                .orElse(null);

        if (user != null) {
            return user;
        }

        // 2) 동일 이메일로 가입된 유저 확인
        User existingByEmail = userRepository.findByEmail(userInfo.getEmail()).orElse(null);

        if (existingByEmail != null) {
            String existingProvider = existingByEmail.getProvider() == null ? "LOCAL" : existingByEmail.getProvider();
            if (!provider.equals(existingProvider)) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("provider_mismatch"),
                        "이 이메일은 [" + existingProvider + "] 계정으로 이미 가입되어 있습니다.");
            }
            // 동일 provider인데 providerId가 없는 경우 (마이그레이션 케이스) — 업데이트
            existingByEmail.setProviderId(userInfo.getProviderId());
            return userRepository.save(existingByEmail);
        }

        // 3) 신규 가입
        return registerOAuth2User(userInfo, provider);
    }

    private User registerOAuth2User(OAuth2UserInfo userInfo, String provider) {
        String nickname = generateUniqueNickname(userInfo.getName());

        User user = User.builder()
                .email(userInfo.getEmail())
                .nickname(nickname)
                .name(userInfo.getName())
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // OAuth 유저는 사용 불가한 랜덤 패스워드
                .provider(provider)
                .providerId(userInfo.getProviderId())
                .status(User.UserStatus.ACTIVE)
                .role(User.UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("OAuth2 신규 가입: email={}, provider={}, nickname={}", savedUser.getEmail(), provider, nickname);
        return savedUser;
    }

    private String generateUniqueNickname(String baseName) {
        if (baseName == null || baseName.isBlank()) baseName = "user";

        // 특수문자/공백 제거, 최대 15자
        String cleaned = baseName.replaceAll("[^a-zA-Z0-9가-힣]", "");
        if (cleaned.isBlank()) cleaned = "user";
        String base = cleaned.substring(0, Math.min(cleaned.length(), 15));

        String nickname = base;
        int attempt = 0;
        while (userRepository.existsByNickname(nickname)) {
            nickname = base + (int) (Math.random() * 9000 + 1000);
            if (++attempt > 10) {
                nickname = base + System.currentTimeMillis();
                break;
            }
        }
        return nickname;
    }
}
