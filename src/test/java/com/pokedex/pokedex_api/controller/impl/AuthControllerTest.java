package com.pokedex.pokedex_api.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokedex.pokedex_api.controller.dto.request.LoginRequest;
import com.pokedex.pokedex_api.controller.dto.request.RegisterRequest;
import com.pokedex.pokedex_api.controller.dto.response.UserResponse;
import com.pokedex.pokedex_api.controller.mapper.UserDtoMapper;
import com.pokedex.pokedex_api.core.model.Role;
import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.core.service.interfaces.UserService;
import com.pokedex.pokedex_api.security.JwtService;
import com.pokedex.pokedex_api.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = OAuth2ClientAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = {
                        "com.pokedex.pokedex_api.security..*",
                        "com.pokedex.pokedex_api.config..*"
                }))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private UserDtoMapper userMapper;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private JwtService jwtService;

    @Test
    void register_withValidBody_returns201() throws Exception {
        RegisterRequest request = new RegisterRequest("ash", "ash@pokemon.com", "Secret123");
        User created = User.builder().id(1L).email("ash@pokemon.com").username("ash").role(Role.TRAINER).build();
        UserResponse response = new UserResponse(1L, "ash@pokemon.com", "ash", "TRAINER", true);

        when(userService.register(any(), eq("Secret123"))).thenReturn(created);
        when(userMapper.toResponse(created)).thenReturn(response);

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ash@pokemon.com"));
    }

    @Test
    void register_withInvalidBody_returns400() throws Exception {
        RegisterRequest invalid = new RegisterRequest("as", "not-an-email", "123");

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_withValidCredentials_returnsToken() throws Exception {
        LoginRequest request = new LoginRequest("ash@pokemon.com", "Secret123");
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("ash@pokemon.com").password("hash")
                .authorities(new SimpleGrantedAuthority("ROLE_TRAINER")).build();
        User user = User.builder().id(1L).email("ash@pokemon.com").role(Role.TRAINER).build();

        when(userDetailsService.loadUserByUsername("ash@pokemon.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");
        when(userService.findByEmail("ash@pokemon.com")).thenReturn(user);

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.role").value("TRAINER"));
    }

    @Test
    void login_withInvalidCredentials_returns401() throws Exception {
        LoginRequest request = new LoginRequest("ash@pokemon.com", "wrong");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"));
    }
}
