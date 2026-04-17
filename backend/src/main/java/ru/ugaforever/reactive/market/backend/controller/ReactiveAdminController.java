package ru.ugaforever.reactive.market.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j  // (log)
@RestController
@RequiredArgsConstructor
public class ReactiveAdminController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<String> adminEndpoint() {
        return Mono.just("Admin-only endpoint");
    }
}
