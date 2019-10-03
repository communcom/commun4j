package io.golos.commun4j.model;

public class ResolvedName {
    private String resolved_domain;
    private String resolved_username;

    public ResolvedName(String resolved_domain, String resolved_username) {
        this.resolved_domain = resolved_domain;
        this.resolved_username = resolved_username;
    }

    public String getResolved_domain() {
        return resolved_domain;
    }

    public void setResolved_domain(String resolved_domain) {
        this.resolved_domain = resolved_domain;
    }

    public String getResolved_username() {
        return resolved_username;
    }

    public void setResolved_username(String resolved_username) {
        this.resolved_username = resolved_username;
    }

    @Override
    public String toString() {
        return "ResolvedName{" +
                "resolved_domain='" + resolved_domain + '\'' +
                ", resolved_username='" + resolved_username + '\'' +
                '}';
    }
}
