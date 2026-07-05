package com.pokedex.pokedex_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Separado de SecurityConfig a propósito: SecurityConfig depende (vía
 * constructor) de OAuth2SuccessHandler, y OAuth2SuccessHandler depende de
 * PasswordEncoder. Si el bean PasswordEncoder viviera dentro de
 * SecurityConfig, Spring necesitaría terminar de construir SecurityConfig
 * para exponer el bean que SecurityConfig mismo necesita primero ->
 * referencia circular (BeanCurrentlyInCreationException).
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
