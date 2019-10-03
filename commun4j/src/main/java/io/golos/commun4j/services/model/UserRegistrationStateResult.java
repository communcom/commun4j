package io.golos.commun4j.services.model;

import com.squareup.moshi.Json;

import java.util.Objects;

public class UserRegistrationStateResult {
    @Json(name = "currentState")
    private UserRegistrationState state;
    private String user;


    public UserRegistrationStateResult(UserRegistrationState state, String user) {
        this.state = state;
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public UserRegistrationState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRegistrationStateResult that = (UserRegistrationStateResult) o;
        return state == that.state &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, user);
    }

    public void setState(UserRegistrationState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "UserRegistrationStateResult{" +
                "state=" + state +
                ", name=" + user +
                '}';
    }
}
