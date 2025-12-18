package com.librarian.todo_list.user.repository;

import com.librarian.todo_list.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 사용자 조회
     */
    Optional<User> findByNickname(String nickname);
    
    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 사용자명 또는 이메일로 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.nickname = :identifier OR u.email = :identifier")
    Optional<User> findByNicknameOrEmail(@Param("identifier") String identifier);
    
    /**
     * 사용자명 존재 여부 확인
     */
    boolean existsByNickname(String nickname);
    
    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
    
    /**
     * 활성 상태인 사용자들만 조회
     */
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    java.util.List<User> findActiveUsers();
    
    /**
     * 사용자명과 이메일로 사용자 조회 (중복 검사용)
     */
    @Query("SELECT u FROM User u WHERE u.nickname = :nickname OR u.email = :email")
    Optional<User> findByNicknameOrEmailForDuplicateCheck(@Param("nickname") String nickname, @Param("email") String email);
}