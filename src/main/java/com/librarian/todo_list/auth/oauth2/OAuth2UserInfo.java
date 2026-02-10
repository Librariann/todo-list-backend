package com.librarian.todo_list.auth.oauth2;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();
}
