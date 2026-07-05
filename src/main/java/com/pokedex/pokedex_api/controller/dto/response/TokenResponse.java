package com.pokedex.pokedex_api.controller.dto.response;

public record TokenResponse(
        String token,
        String email,
        String role
) {}
