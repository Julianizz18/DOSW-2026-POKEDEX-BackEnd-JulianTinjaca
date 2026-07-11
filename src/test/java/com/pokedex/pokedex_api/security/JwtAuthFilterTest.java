package com.pokedex.pokedex_api.security;

import com.pokedex.pokedex_api.core.model.Role;
import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.core.port.UserPersistencePort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserPersistencePort userPort;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_withoutAuthHeader_skipsAuthentication() throws Exception {
        JwtAuthFilter filter = new JwtAuthFilter(jwtService, new UserDetailsServiceImpl(userPort));
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_withValidToken_setsAuthentication() throws Exception {
        JwtAuthFilter filter = new JwtAuthFilter(jwtService, new UserDetailsServiceImpl(userPort));
        User user = User.builder().id(1L).email("ash@pokemon.com").username("ash")
                .passwordHash("hash").role(Role.TRAINER).enabled(true).build();

        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtService.extractUsername("valid-token")).thenReturn("ash@pokemon.com");
        when(userPort.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(eq("valid-token"), any())).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("ash@pokemon.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_withInvalidToken_doesNotSetAuthentication() throws Exception {
        JwtAuthFilter filter = new JwtAuthFilter(jwtService, new UserDetailsServiceImpl(userPort));
        User user = User.builder().id(1L).email("ash@pokemon.com").username("ash")
                .passwordHash("hash").role(Role.TRAINER).enabled(true).build();

        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtService.extractUsername("invalid-token")).thenReturn("ash@pokemon.com");
        when(userPort.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(eq("invalid-token"), any())).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilter_whenJwtServiceThrows_clearsContextAndContinues() throws Exception {
        JwtAuthFilter filter = new JwtAuthFilter(jwtService, new UserDetailsServiceImpl(userPort));

        when(request.getHeader("Authorization")).thenReturn("Bearer broken-token");
        when(jwtService.extractUsername("broken-token")).thenThrow(new RuntimeException("bad token"));

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
