package com.pokedex.pokedex_api.controller.mapper;

import com.pokedex.pokedex_api.controller.dto.response.UserResponse;
import com.pokedex.pokedex_api.core.model.Role;
import com.pokedex.pokedex_api.core.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoMapperTest {

    private final UserDtoMapper mapper = new UserDtoMapperImpl();

    @Test
    void toResponse_mapsRoleAsString() {
        User user = User.builder().id(1L).email("a@a.com").username("ash").role(Role.TRAINER).enabled(true).build();

        UserResponse response = mapper.toResponse(user);

        assertThat(response.role()).isEqualTo("TRAINER");
        assertThat(response.email()).isEqualTo("a@a.com");
    }

    @Test
    void toResponse_null_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
