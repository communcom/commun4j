package io.golos.commun4j.services.model;

import java.util.Objects;

import io.golos.commun4j.sharedmodel.CyberName;

public class RegisterResult {
    private CyberName userId;
    private String username;

    public RegisterResult(CyberName userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public CyberName getUserId() {
        return userId;
    }

    public void setUserId(CyberName userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterResult that = (RegisterResult) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }

    @Override
    public String toString() {
        return "RegisterResult{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                '}';
    }
}
