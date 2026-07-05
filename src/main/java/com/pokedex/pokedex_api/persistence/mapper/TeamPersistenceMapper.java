package com.pokedex.pokedex_api.persistence.mapper;

import com.pokedex.pokedex_api.core.model.Team;
import com.pokedex.pokedex_api.persistence.entity.relational.TeamEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamPersistenceMapper {
    Team toDomain(TeamEntity entity);

    @Mapping(target = "id", ignore = true)
    TeamEntity toEntity(Team team);
}
