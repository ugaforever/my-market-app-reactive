package ru.ugaforever.reactive.market.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactiveAccountIdService {

    public Mono<Authentication> getCurrentAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated());
    }
    //UUID Keycloak
    public Mono<String> getCurrentUserId() {
        return getCurrentAuthentication()
                .map(Authentication::getName);
    }

    public Mono<String> getCurrentUsername() {
        return getCurrentAuthentication()
                .filter(auth -> auth.getPrincipal() instanceof OAuth2User)
                .map(auth -> (OAuth2User) auth.getPrincipal())
                .map(user -> user.getAttribute("preferred_username"))
                .cast(String.class);
    }
}
