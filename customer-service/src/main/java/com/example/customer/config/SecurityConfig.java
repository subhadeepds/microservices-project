package com.example.customer.config;

import com.example.customer.security.HeaderAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final HeaderAuthenticationFilter headerFilter;

    public SecurityConfig(HeaderAuthenticationFilter headerFilter) {
        this.headerFilter = headerFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(headerFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // Allow reading by all authenticated users
                .requestMatchers("/customers/**").hasAnyRole("USER", "ADMIN")
                // Allow adding/updating/deleting only for ADMIN
                .requestMatchers("/customers/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
