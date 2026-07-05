package com.pokedex.pokedex_api.controller.api;

import com.pokedex.pokedex_api.controller.dto.request.LoginRequest;
import com.pokedex.pokedex_api.controller.dto.request.RegisterRequest;
import com.pokedex.pokedex_api.controller.dto.response.TokenResponse;
import com.pokedex.pokedex_api.controller.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Auth", description = "Registro y autenticación de usuarios")
@RequestMapping("/v1/auth")
public interface AuthApi {

    @Operation(summary = "Registro de usuario (RF-01)")
    @PostMapping("/register")
    ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request);

    @Operation(summary = "Inicio de sesión con credenciales (RF-02)")
    @PostMapping("/login")
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request);
}
