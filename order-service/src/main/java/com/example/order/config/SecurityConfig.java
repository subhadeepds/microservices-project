package com.example.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password("password").roles("ADMIN").build(),
                User.withUsername("user").password("1234").roles("USER").build()
        );
    }

    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/orders/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/orders/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/orders/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/orders/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic();
        return http.build();
    }
}
