package com.pokedex.pokedex_api.controller.mapper;

import com.pokedex.pokedex_api.controller.dto.request.PokemonRequest;
import com.pokedex.pokedex_api.controller.dto.response.PokemonResponse;
import com.pokedex.pokedex_api.controller.dto.response.PokemonStatsResponse;
import com.pokedex.pokedex_api.core.model.Pokemon;
import com.pokedex.pokedex_api.core.model.PokemonStats;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PokemonDtoMapper {

    PokemonResponse toResponse(Pokemon pokemon);

    @Mapping(target = "total", expression = "java(stats.getTotal())")
    PokemonStatsResponse toResponse(PokemonStats stats);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hasMega", expression = "java(request.hasMega() != null ? request.hasMega() : false)")
    Pokemon toDomain(PokemonRequest request);

    PokemonStats toDomain(com.pokedex.pokedex_api.controller.dto.request.PokemonStatsRequest request);
}
