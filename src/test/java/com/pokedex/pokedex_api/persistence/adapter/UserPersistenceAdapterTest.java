package com.pokedex.pokedex_api.persistence.adapter;

import com.pokedex.pokedex_api.core.model.Role;
import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.persistence.entity.relational.RoleEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.UserEntity;
import com.pokedex.pokedex_api.persistence.mapper.UserPersistenceMapperImpl;
import com.pokedex.pokedex_api.persistence.repository.relational.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPersistenceAdapterTest {

    @Mock
    private UserJpaRepository userRepository;

    private UserPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserPersistenceAdapter(userRepository, new UserPersistenceMapperImpl());
    }

    @Test
    void findById_whenExists_mapsToDomain() {
        UserEntity entity = UserEntity.builder().id(1L).email("a@a.com").username("ash")
                .passwordHash("hash").role(RoleEntity.TRAINER).enabled(true).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThat(adapter.findById(1L)).isPresent();
    }

    @Test
    void findByEmail_whenExists_mapsToDomain() {
        UserEntity entity = UserEntity.builder().id(1L).email("a@a.com").username("ash")
                .passwordHash("hash").role(RoleEntity.TRAINER).enabled(true).build();
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(entity));

        assertThat(adapter.findByEmail("a@a.com")).isPresent();
    }

    @Test
    void existsByEmail_delegates() {
        when(userRepository.existsByEmail("a@a.com")).thenReturn(true);

        assertThat(adapter.existsByEmail("a@a.com")).isTrue();
    }

    @Test
    void save_whenNew_doesNotForceId() {
        User user = User.builder().email("a@a.com").username("ash").passwordHash("hash")
                .role(Role.TRAINER).enabled(true).build();
        UserEntity saved = UserEntity.builder().id(5L).email("a@a.com").username("ash")
                .passwordHash("hash").role(RoleEntity.TRAINER).enabled(true).build();
        when(userRepository.save(any())).thenReturn(saved);

        User result = adapter.save(user);

        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    void save_whenExisting_keepsId() {
        User user = User.builder().id(1L).email("a@a.com").username("ash").passwordHash("hash")
                .role(Role.TRAINER).enabled(true).build();
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = adapter.save(user);

        assertThat(result.getId()).isEqualTo(1L);
    }
}
