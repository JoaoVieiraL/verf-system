package com.verf_system.verfS.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())              // API REST não usa sessão de navegador — CSRF não se aplica aqui
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()               // temporário: libera tudo pra você testar os CRUDs
                );
        return http.build();
    }
}