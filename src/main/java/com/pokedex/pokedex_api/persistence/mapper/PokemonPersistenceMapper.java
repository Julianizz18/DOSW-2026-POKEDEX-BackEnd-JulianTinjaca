package com.pokedex.pokedex_api.persistence.mapper;

import com.pokedex.pokedex_api.core.model.Pokemon;
import com.pokedex.pokedex_api.core.model.PokemonStats;
import com.pokedex.pokedex_api.persistence.entity.relational.PokemonEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.PokemonStatsEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.TypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PokemonPersistenceMapper {

    @Mapping(target = "region", expression = "java(entity.getRegion() != null ? entity.getRegion().getName() : null)")
    @Mapping(target = "types", expression = "java(mapTypeNames(entity.getTypes()))")
    @Mapping(target = "stats", source = "stats")
    @Mapping(target = "description", ignore = true)
    Pokemon toDomain(PokemonEntity entity);

    PokemonStats toDomain(PokemonStatsEntity entity);

    default List<String> mapTypeNames(List<TypeEntity> types) {
        if (types == null) return List.of();
        return types.stream().map(TypeEntity::getName).toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "types", ignore = true)
    @Mapping(target = "region", ignore = true)
    @Mapping(target = "stats", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PokemonEntity toNewEntity(Pokemon pokemon);
}
