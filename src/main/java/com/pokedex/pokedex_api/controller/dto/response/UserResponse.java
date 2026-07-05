package com.pokedex.pokedex_api.controller.dto.response;

public record UserResponse(
        Long id,
        String email,
        String username,
        String role,
        Boolean enabled
) {}
