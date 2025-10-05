package com.example.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Define in-memory users
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password("password")
                        .roles("ADMIN")
                        .build(),
                User.withUsername("user")
                        .password("1234")
                        .roles("USER")
                        .build()
        );
    }

    // Define password encoder (NoOp for demo; don't use in prod!)
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

    // Define security filter chain (authorization rules)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Role-based access
                .requestMatchers(HttpMethod.GET, "/customers/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/customers/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(); // Basic Auth
        return http.build();
    }
}
