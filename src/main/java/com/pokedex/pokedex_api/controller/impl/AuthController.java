package com.pokedex.pokedex_api.controller.impl;

import com.pokedex.pokedex_api.controller.api.AuthApi;
import com.pokedex.pokedex_api.controller.dto.request.LoginRequest;
import com.pokedex.pokedex_api.controller.dto.request.RegisterRequest;
import com.pokedex.pokedex_api.controller.dto.response.TokenResponse;
import com.pokedex.pokedex_api.controller.dto.response.UserResponse;
import com.pokedex.pokedex_api.controller.mapper.UserDtoMapper;
import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.core.service.interfaces.UserService;
import com.pokedex.pokedex_api.security.JwtService;
import com.pokedex.pokedex_api.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final UserService userService;
    private final UserDtoMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<UserResponse> register(RegisterRequest request) {
        User toCreate = User.builder()
                .email(request.email())
                .username(request.username())
                .build();
        User created = userService.register(toCreate, request.password());
        return ResponseEntity.status(201).body(userMapper.toResponse(created));
    }

    @Override
    public ResponseEntity<TokenResponse> login(LoginRequest request) {
        // Si las credenciales son inválidas, lanza BadCredentialsException,
        // que GlobalExceptionHandler traduce a 401 sin revelar cuál campo falló.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);
        User user = userService.findByEmail(request.email());

        return ResponseEntity.ok(new TokenResponse(token, user.getEmail(), user.getRole().name()));
    }
}
