package com.pokedex.pokedex_api.security;

import com.pokedex.pokedex_api.core.model.Role;
import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.core.port.UserPersistencePort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @Mock
    private UserPersistencePort userPort;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;
    @Mock
    private OAuth2User oAuth2User;

    @Test
    void onAuthenticationSuccess_whenUserExists_reusesUserAndWritesToken() throws Exception {
        OAuth2SuccessHandler handler = new OAuth2SuccessHandler(userPort, jwtService, passwordEncoder);
        User existing = User.builder().id(1L).email("ash@gmail.com").username("Ash")
                .passwordHash("hash").role(Role.TRAINER).enabled(true).build();

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("ash@gmail.com");
        when(oAuth2User.getAttribute("name")).thenReturn("Ash");
        when(userPort.findByEmail("ash@gmail.com")).thenReturn(Optional.of(existing));
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userPort, never()).save(any());
        assertThat(sw.toString()).contains("jwt-token").contains("TRAINER");
    }

    @Test
    void onAuthenticationSuccess_whenUserIsNew_createsUser() throws Exception {
        OAuth2SuccessHandler handler = new OAuth2SuccessHandler(userPort, jwtService, passwordEncoder);

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("misty@gmail.com");
        when(oAuth2User.getAttribute("name")).thenReturn(null);
        when(userPort.findByEmail("misty@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("random-hash");
        when(userPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userPort).save(argThat(u -> u.getRole() == Role.TRAINER && u.getUsername().equals("misty@gmail.com")));
        assertThat(sw.toString()).contains("jwt-token");
    }
}
