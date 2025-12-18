package com.librarian.todo_list.user.service;

import com.librarian.todo_list.user.dto.UserRegistrationRequest;
import com.librarian.todo_list.user.dto.UserResponse;
import com.librarian.todo_list.user.entity.User;
import com.librarian.todo_list.exception.InvalidPasswordException;
import com.librarian.todo_list.exception.UserAlreadyExistsException;
import com.librarian.todo_list.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 사용자 회원가입
     */
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        log.info("회원가입 요청: username={}, email={}", request.getNickname(), request.getEmail());
        
        // 비밀번호 일치 확인
        validatePasswordConfirmation(request.getPassword(), request.getConfirmPassword());
        
        // 중복 사용자 확인
        validateUserUniqueness(request.getNickname(), request.getEmail());
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        // User 엔티티 생성
        User user = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .status(User.UserStatus.ACTIVE)
                .role(User.UserRole.USER)
                .build();
        
        // 사용자 저장
        User savedUser = userRepository.save(user);
        log.info("회원가입 완료: userId={}, username={}", savedUser.getId(), savedUser.getNickname());
        
        return UserResponse.from(savedUser);
    }
    
    /**
     * 사용자명으로 사용자 조회
     */
    public Optional<UserResponse> findByNickname(String username) {
        return userRepository.findByNickname(username)
                .map(UserResponse::from);
    }
    
    /**
     * 이메일로 사용자 조회
     */
    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::from);
    }
    
    /**
     * 사용자 ID로 조회
     */
    public Optional<UserResponse> findById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::from);
    }
    
    /**
     * 모든 활성 사용자 조회
     */
    public List<UserResponse> findAllActiveUsers() {
        return userRepository.findActiveUsers()
                .stream()
                .map(UserResponse::from)
                .toList();
    }
    
    /**
     * 사용자명 중복 확인
     */
    public boolean isnicknameExists(String username) {
        return userRepository.existsByNickname(username);
    }
    
    /**
     * 이메일 중복 확인
     */
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * 비밀번호 확인 검증
     */
    private void validatePasswordConfirmation(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new InvalidPasswordException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    }
    
    /**
     * 사용자 중복성 검증
     */
    private void validateUserUniqueness(String username, String email) {
        if (userRepository.existsByNickname(username)) {
            throw new UserAlreadyExistsException("이미 사용 중인 사용자명입니다: " + username);
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("이미 사용 중인 이메일입니다: " + email);
        }
    }
}