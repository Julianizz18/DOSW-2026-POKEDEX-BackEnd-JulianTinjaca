package com.pokedex.pokedex_api.persistence.mapper;

import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.persistence.entity.relational.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
    User toDomain(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    UserEntity toEntity(User user);
}
