package com.example.order.config;

import com.example.order.security.HeaderAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(new HeaderAuthenticationFilter(),
                    org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // ✅ USER can create and view orders
                .requestMatchers("/orders/**").hasAnyRole("USER", "ADMIN")

                // ✅ ADMIN can delete or update any order
                .requestMatchers("/orders/delete/**", "/orders/update/**").hasRole("ADMIN")

                // everything else requires authentication
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}
