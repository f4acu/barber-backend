package com.barber.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity // ← IMPORTANTE: Habilita @PreAuthorize
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    )
        .authorizeHttpRequests(auth -> auth
        // ✅ Rutas públicas
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/barbershops/**").permitAll()
        .requestMatchers("/api/payments/webhook").permitAll() // ← NUEVO
    
        // GET públicos
        .requestMatchers("GET", "/professionals").permitAll()
        .requestMatchers("GET", "/services").permitAll()
    
        // 🔒 Todo lo demás requiere autenticación
        .anyRequest().authenticated()
    )
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable());

        return http.build();
    }
}