package com.example.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * This filter adds secure headers and logs user forwarding.
 */
@Component
public class AuthHeaderFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AuthHeaderFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        HttpHeaders headers = exchange.getRequest().getHeaders();
        String username = headers.getFirst("X-Auth-User");
        String roles = headers.getFirst("X-Auth-Roles");

        if (username != null) {
            logger.info("🔐 Forwarding request as user '{}', roles = [{}]", username, roles);
        } else {
            logger.warn("⚠️ No authenticated user info found in headers — possible misconfiguration!");
        }

        // Add a security header for traceability
        ServerWebExchange mutated = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-Gateway-Checked", "true")
                        .build())
                .build();

        return chain.filter(mutated);
    }

    @Override
    public int getOrder() {
        return 1; // Run after LoggingGlobalFilter (order 0)
    }
}
