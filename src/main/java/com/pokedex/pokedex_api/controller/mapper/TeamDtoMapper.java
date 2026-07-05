package com.pokedex.pokedex_api.controller.mapper;

import com.pokedex.pokedex_api.controller.dto.request.TeamRequest;
import com.pokedex.pokedex_api.controller.dto.response.TeamResponse;
import com.pokedex.pokedex_api.core.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamDtoMapper {

    TeamResponse toResponse(Team team);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Team toDomain(TeamRequest request);
}
