package com.example.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Define in-memory users
    @Bean
    public MapReactiveUserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new MapReactiveUserDetailsService(
                User.withUsername("admin").password(encoder.encode("adminpass")).roles("ADMIN", "USER").build(),
                User.withUsername("user").password(encoder.encode("userpass")).roles("USER").build()
        );
    }

    // ✅ Gateway-level Basic Auth config
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange
                    .pathMatchers("/eureka/**", "/actuator/**").permitAll()
                    .anyExchange().authenticated()
            )
            .httpBasic(basic -> {}) // enable HTTP Basic Auth
            .formLogin(form -> form.disable());

        return http.build();
    }

    // ✅ Add Auth headers to downstream requests
    @Bean
    public GlobalFilter addAuthHeadersFilter() {
        return (exchange, chain) ->
            exchange.getPrincipal()
                    .flatMap(principal -> {
                        if (principal instanceof org.springframework.security.core.Authentication auth) {
                            String username = auth.getName();
                            String roles = auth.getAuthorities().stream()
                                    .map(Object::toString)
                                    .collect(Collectors.joining(","));

                            ServerWebExchange mutated = exchange.mutate()
                                    .request(exchange.getRequest().mutate()
                                            .header("X-Auth-User", username)
                                            .header("X-Auth-Roles", roles)
                                            .build())
                                    .build();

                            return chain.filter(mutated);
                        }
                        return chain.filter(exchange);
                    })
                    .switchIfEmpty(chain.filter(exchange));
    }
}
