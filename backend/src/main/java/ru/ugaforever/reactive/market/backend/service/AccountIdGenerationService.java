package ru.ugaforever.reactive.market.backend.service;

import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;

@Component
public class AccountIdGenerationService {
    public String generate(WebSession session) {

        Long accountId = session.getAttribute("accountId");
        if (accountId == null) {
            accountId = System.currentTimeMillis() + (long) (Math.random() * 10000);
            session.getAttributes().put("accountId", accountId);
        }
        return accountId.toString();
    }
}
