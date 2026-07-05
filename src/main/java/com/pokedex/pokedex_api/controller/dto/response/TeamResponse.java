package com.pokedex.pokedex_api.controller.dto.response;

import java.util.List;

public record TeamResponse(
        Long id,
        Long userId,
        String name,
        List<Long> pokemonIds
) {}
