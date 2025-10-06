package com.example.product.config;

import com.example.product.security.HeaderAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
            .addFilterBefore(new HeaderAuthenticationFilter(),
                    org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // Allow only ADMIN to modify products
                .requestMatchers("/products/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/products/add", "/products/update/**", "/products/delete/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> httpBasic.disable()); // We use gateway headers, not HTTP Basic

        return http.build();
    }
}
