package com.pokedex.pokedex_api.controller.mapper;

import com.pokedex.pokedex_api.controller.dto.response.FavoriteResponse;
import com.pokedex.pokedex_api.core.model.Favorite;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FavoriteDtoMapper {
    FavoriteResponse toResponse(Favorite favorite);
}
