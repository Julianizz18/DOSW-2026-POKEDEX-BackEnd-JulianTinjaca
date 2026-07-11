package com.pokedex.pokedex_api.core.service.impl;

import com.pokedex.pokedex_api.core.exception.DuplicateResourceException;
import com.pokedex.pokedex_api.core.exception.ResourceNotFoundException;
import com.pokedex.pokedex_api.core.model.Role;
import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.core.port.UserPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserPersistencePort userPort;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void findById_whenExists_returnsUser() {
        User user = User.builder().id(1L).email("a@a.com").build();
        when(userPort.findById(1L)).thenReturn(Optional.of(user));

        assertThat(service.findById(1L)).isEqualTo(user);
    }

    @Test
    void findById_whenNotFound_throws() {
        when(userPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void findByEmail_whenExists_returnsUser() {
        User user = User.builder().id(1L).email("a@a.com").build();
        when(userPort.findByEmail("a@a.com")).thenReturn(Optional.of(user));

        assertThat(service.findByEmail("a@a.com")).isEqualTo(user);
    }

    @Test
    void findByEmail_whenNotFound_throws() {
        when(userPort.findByEmail("x@x.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByEmail("x@x.com"));
    }

    @Test
    void register_whenEmailNew_encodesPasswordAndForcesTrainerRole() {
        User toRegister = User.builder().email("a@a.com").username("ash").role(Role.ADMIN).build();
        when(userPort.existsByEmail("a@a.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123")).thenReturn("hashed");
        when(userPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = service.register(toRegister, "Secret123");

        assertThat(result.getRole()).isEqualTo(Role.TRAINER);
        assertThat(result.getPasswordHash()).isEqualTo("hashed");
        assertThat(result.getEnabled()).isTrue();
    }

    @Test
    void register_whenEmailExists_throws() {
        User toRegister = User.builder().email("a@a.com").build();
        when(userPort.existsByEmail("a@a.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.register(toRegister, "Secret123"));
        verify(userPort, never()).save(any());
    }

    @Test
    void updateProfile_onlyUpdatesUsername() {
        User existing = User.builder().id(1L).email("a@a.com").username("old")
                .passwordHash("hash").role(Role.TRAINER).enabled(true).build();
        User patch = User.builder().username("new").build();
        when(userPort.findById(1L)).thenReturn(Optional.of(existing));
        when(userPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = service.updateProfile(1L, patch);

        assertThat(result.getUsername()).isEqualTo("new");
        assertThat(result.getEmail()).isEqualTo("a@a.com");
        assertThat(result.getPasswordHash()).isEqualTo("hash");
    }
}
