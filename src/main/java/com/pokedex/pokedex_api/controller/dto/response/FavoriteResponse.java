package com.pokedex.pokedex_api.controller.dto.response;

import java.time.LocalDateTime;

public record FavoriteResponse(
        Long id,
        Long userId,
        Long pokemonId,
        LocalDateTime addedAt
) {}
