package com.pokedex.pokedex_api.persistence.mapper;

import com.pokedex.pokedex_api.core.model.Role;
import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.persistence.entity.relational.RoleEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.UserEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPersistenceMapperTest {

    private final UserPersistenceMapper mapper = new UserPersistenceMapperImpl();

    @Test
    void toDomain_mapsRole() {
        UserEntity entity = UserEntity.builder().id(1L).email("a@a.com").username("ash")
                .passwordHash("hash").role(RoleEntity.TRAINER).enabled(true).build();

        User user = mapper.toDomain(entity);

        assertThat(user.getRole()).isEqualTo(Role.TRAINER);
        assertThat(user.getEmail()).isEqualTo("a@a.com");
    }

    @Test
    void toDomain_null_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toEntity_ignoresIdAndMapsRole() {
        User user = User.builder().id(9L).email("a@a.com").username("ash")
                .passwordHash("hash").role(Role.ADMIN).enabled(true).build();

        UserEntity entity = mapper.toEntity(user);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getRole()).isEqualTo(RoleEntity.ADMIN);
    }

    @Test
    void toEntity_null_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }
}
