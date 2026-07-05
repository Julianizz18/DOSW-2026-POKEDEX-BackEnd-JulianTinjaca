package com.pokedex.pokedex_api.controller.impl;

import com.pokedex.pokedex_api.controller.api.FavoriteApi;
import com.pokedex.pokedex_api.controller.dto.response.FavoriteResponse;
import com.pokedex.pokedex_api.controller.mapper.FavoriteDtoMapper;
import com.pokedex.pokedex_api.core.service.interfaces.FavoriteService;
import com.pokedex.pokedex_api.core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FavoriteController implements FavoriteApi {

    private final FavoriteService favoriteService;
    private final FavoriteDtoMapper mapper;
    private final UserService userService;

    @Override
    public ResponseEntity<List<FavoriteResponse>> findMyFavorites(Authentication authentication) {
        Long userId = userService.findByEmail(authentication.getName()).getId();
        List<FavoriteResponse> favorites = favoriteService.findByUserId(userId).stream()
                .map(mapper::toResponse).toList();
        return ResponseEntity.ok(favorites);
    }

    @Override
    public ResponseEntity<FavoriteResponse> add(Long pokemonId, Authentication authentication) {
        Long userId = userService.findByEmail(authentication.getName()).getId();
        return ResponseEntity.status(201).body(mapper.toResponse(favoriteService.add(userId, pokemonId)));
    }

    @Override
    public ResponseEntity<Void> remove(Long pokemonId, Authentication authentication) {
        Long userId = userService.findByEmail(authentication.getName()).getId();
        favoriteService.remove(userId, pokemonId);
        return ResponseEntity.noContent().build();
    }
}
