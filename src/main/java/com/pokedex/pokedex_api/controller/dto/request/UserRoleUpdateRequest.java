package com.pokedex.pokedex_api.controller.dto.request;

public record UserRoleUpdateRequest(
        String role,
        Boolean enabled
) {}
