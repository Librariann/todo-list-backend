package com.librarian.todo_list.auth.service;

import com.librarian.todo_list.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String SESSION_PREFIX = "session:";
    private static final String USER_SESSION_PREFIX = "user:session:";
    private static final Duration SESSION_TIMEOUT = Duration.ofDays(7);
    
    public String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        String sessionKey = SESSION_PREFIX + sessionId;
        String userSessionKey = USER_SESSION_PREFIX + user.getId();
        
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("userId", user.getId());
        sessionData.put("email", user.getEmail());
        sessionData.put("nickname", user.getNickname());
        sessionData.put("createdAt", System.currentTimeMillis());
        
        redisTemplate.opsForValue().set(sessionKey, sessionData, SESSION_TIMEOUT);
        redisTemplate.opsForValue().set(userSessionKey, sessionId, SESSION_TIMEOUT);
        
        log.info("Session created for user: {}, sessionId: {}", user.getEmail(), sessionId);
        return sessionId;
    }
    
    public Map<String, Object> getSession(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        
        String sessionKey = SESSION_PREFIX + sessionId;
        Object sessionData = redisTemplate.opsForValue().get(sessionKey);
        
        if (sessionData instanceof Map) {
            return (Map<String, Object>) sessionData;
        }
        return null;
    }
    
    public boolean isValidSession(String sessionId) {
        Map<String, Object> sessionData = getSession(sessionId);
        return sessionData != null;
    }
    
    public void invalidateSession(String sessionId) {
        if (sessionId == null) {
            return;
        }
        
        Map<String, Object> sessionData = getSession(sessionId);
        if (sessionData != null) {
            String sessionKey = SESSION_PREFIX + sessionId;
            Long userId = (Long) sessionData.get("userId");
            String userSessionKey = USER_SESSION_PREFIX + userId;
            
            redisTemplate.delete(sessionKey);
            redisTemplate.delete(userSessionKey);
            
            log.info("Session invalidated: {}", sessionId);
        }
    }
    
    public void invalidateUserSessions(Long userId) {
        String userSessionKey = USER_SESSION_PREFIX + userId;
        String sessionId = (String) redisTemplate.opsForValue().get(userSessionKey);
        
        if (sessionId != null) {
            invalidateSession(sessionId);
        }
    }
    
    public void refreshSession(String sessionId) {
        Map<String, Object> sessionData = getSession(sessionId);
        if (sessionData != null) {
            String sessionKey = SESSION_PREFIX + sessionId;
            redisTemplate.expire(sessionKey, SESSION_TIMEOUT);
            
            Long userId = (Long) sessionData.get("userId");
            String userSessionKey = USER_SESSION_PREFIX + userId;
            redisTemplate.expire(userSessionKey, SESSION_TIMEOUT);
        }
    }
}