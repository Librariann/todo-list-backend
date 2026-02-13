package com.librarian.todo_list.auth.oauth2;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getResponse() {
        return (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        Map<String, Object> response = getResponse();
        return response != null ? (String) response.get("id") : null;
    }

    @Override
    public String getEmail() {
        Map<String, Object> response = getResponse();
        return response != null ? (String) response.get("email") : null;
    }

    @Override
    public String getName() {
        Map<String, Object> response = getResponse();
        return response != null ? (String) response.get("name") : null;
    }
}
