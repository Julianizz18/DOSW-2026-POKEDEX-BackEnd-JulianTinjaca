package com.pokedex.pokedex_api.security;

import com.pokedex.pokedex_api.core.model.Role;
import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.core.port.UserPersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserPersistencePort userPort;

    @Test
    void loadUserByUsername_whenExists_returnsUserDetails() {
        UserDetailsServiceImpl service = new UserDetailsServiceImpl(userPort);
        User user = User.builder().id(1L).email("ash@pokemon.com").username("ash")
                .passwordHash("hash").role(Role.TRAINER).enabled(true).build();
        when(userPort.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("ash@pokemon.com");

        assertThat(result.getUsername()).isEqualTo("ash@pokemon.com");
        assertThat(result.getAuthorities()).extracting(Object::toString).containsExactly("ROLE_TRAINER");
        assertThat(result.isEnabled()).isTrue();
    }

    @Test
    void loadUserByUsername_whenDisabled_returnsDisabledDetails() {
        UserDetailsServiceImpl service = new UserDetailsServiceImpl(userPort);
        User user = User.builder().id(1L).email("ash@pokemon.com").username("ash")
                .passwordHash("hash").role(Role.TRAINER).enabled(false).build();
        when(userPort.findByEmail("ash@pokemon.com")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("ash@pokemon.com");

        assertThat(result.isEnabled()).isFalse();
    }

    @Test
    void loadUserByUsername_whenNotFound_throws() {
        UserDetailsServiceImpl service = new UserDetailsServiceImpl(userPort);
        when(userPort.findByEmail("x@x.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("x@x.com"));
    }
}
