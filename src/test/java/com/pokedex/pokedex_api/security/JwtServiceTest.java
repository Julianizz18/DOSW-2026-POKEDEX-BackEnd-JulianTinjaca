package com.pokedex.pokedex_api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "bGv5PgO+CLXL1bPmcxKNBMhGfnj/Y/wj+/rXtRfsvmU=");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("ash@pokemon.com").password("hash")
                .authorities(new SimpleGrantedAuthority("ROLE_TRAINER")).build();
    }

    @Test
    void generateToken_thenExtractUsername_roundTrips() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.extractUsername(token)).isEqualTo("ash@pokemon.com");
    }

    @Test
    void isTokenValid_withMatchingUser_returnsTrue() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_withDifferentUser_returnsFalse() {
        String token = jwtService.generateToken(userDetails);
        UserDetails other = org.springframework.security.core.userdetails.User.builder()
                .username("misty@pokemon.com").password("hash")
                .authorities(new SimpleGrantedAuthority("ROLE_TRAINER")).build();

        assertThat(jwtService.isTokenValid(token, other)).isFalse();
    }

    @Test
    void isTokenValid_withExpiredToken_returnsFalse() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }

    @Test
    void isTokenValid_withMalformedToken_returnsFalse() {
        assertThat(jwtService.isTokenValid("not-a-token", userDetails)).isFalse();
    }
}
