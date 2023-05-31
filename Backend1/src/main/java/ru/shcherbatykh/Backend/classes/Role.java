package ru.shcherbatykh.Backend.classes;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public enum Role  implements GrantedAuthority {
    USER("USER"), TEACHER("TEACHER"), ADMIN("ADMIN");

    private final String value;

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.name()));
    }

    @Override
    public String getAuthority() {
        return this.name();
    }
}