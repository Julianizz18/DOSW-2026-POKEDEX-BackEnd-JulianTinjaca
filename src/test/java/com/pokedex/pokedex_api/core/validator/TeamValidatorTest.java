package com.pokedex.pokedex_api.core.validator;

import com.pokedex.pokedex_api.core.exception.InvalidOperationException;
import com.pokedex.pokedex_api.core.model.Team;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TeamValidatorTest {

    private final TeamValidator validator = new TeamValidator();

    @Test
    void validateCanAddPokemon_whenLessThanSix_doesNotThrow() {
        Team team = Team.builder().name("Equipo Kanto").pokemonIds(List.of(1L, 2L, 3L)).build();

        assertDoesNotThrow(() -> validator.validateCanAddPokemon(team));
    }

    @Test
    void validateCanAddPokemon_whenAlreadySix_throws() {
        Team team = Team.builder().name("Equipo Kanto")
                .pokemonIds(List.of(1L, 2L, 3L, 4L, 5L, 6L)).build();

        assertThrows(InvalidOperationException.class, () -> validator.validateCanAddPokemon(team));
    }

    @Test
    void validateNotDuplicate_whenPokemonAlreadyInTeam_throws() {
        Team team = Team.builder().name("Equipo Kanto").pokemonIds(List.of(25L)).build();

        assertThrows(InvalidOperationException.class, () -> validator.validateNotDuplicate(team, 25L));
    }

    @Test
    void validateNotDuplicate_whenPokemonNotInTeam_doesNotThrow() {
        Team team = Team.builder().name("Equipo Kanto").pokemonIds(List.of(25L)).build();

        assertDoesNotThrow(() -> validator.validateNotDuplicate(team, 1L));
    }
}
