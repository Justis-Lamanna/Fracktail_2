package com.github.lucbui.magic.command.context;

public class UserIdAndUsername {
    private String userId;
    private String username;

    public UserIdAndUsername(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
