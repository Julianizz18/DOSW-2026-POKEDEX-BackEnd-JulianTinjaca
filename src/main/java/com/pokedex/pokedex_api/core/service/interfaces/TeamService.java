package com.pokedex.pokedex_api.core.service.interfaces;

import com.pokedex.pokedex_api.core.model.Team;

import java.util.List;

public interface TeamService {

    List<Team> findByUserId(Long userId);

    Team findById(Long id);

    Team create(Team team);

    Team addPokemon(Long teamId, Long pokemonId);

    Team removePokemon(Long teamId, Long pokemonId);

    void delete(Long id);

    /**
     * Análisis competitivo básico (RF-15): cobertura de tipos del equipo.
     * Devuelve un resumen legible; el detalle completo de matriz de
     * efectividad se deja como evolución futura del endpoint.
     */
    String analyzeTeam(Long teamId);
}
