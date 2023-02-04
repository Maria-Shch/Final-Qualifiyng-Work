package ru.shcherbatykh.Backend.classes;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Set;

public enum Role  implements GrantedAuthority {
    GUEST, USER, TEACHER, ADMIN;

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.name()));
    }

    @Override
    public String getAuthority() {
        return this.name();
    }
}