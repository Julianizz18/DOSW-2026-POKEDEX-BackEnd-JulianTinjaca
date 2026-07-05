package com.pokedex.pokedex_api.persistence.mapper;

import com.pokedex.pokedex_api.core.model.Favorite;
import com.pokedex.pokedex_api.persistence.entity.relational.FavoriteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoritePersistenceMapper {
    Favorite toDomain(FavoriteEntity entity);

    @Mapping(target = "id", ignore = true)
    FavoriteEntity toEntity(Favorite favorite);
}
