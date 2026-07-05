package com.pokedex.pokedex_api.controller.impl;

import com.pokedex.pokedex_api.controller.api.TeamApi;
import com.pokedex.pokedex_api.controller.dto.request.TeamPokemonRequest;
import com.pokedex.pokedex_api.controller.dto.request.TeamRequest;
import com.pokedex.pokedex_api.controller.dto.response.TeamResponse;
import com.pokedex.pokedex_api.controller.mapper.TeamDtoMapper;
import com.pokedex.pokedex_api.core.model.Team;
import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.core.service.interfaces.TeamService;
import com.pokedex.pokedex_api.core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController implements TeamApi {

    private final TeamService teamService;
    private final TeamDtoMapper mapper;
    private final UserService userService;

    @Override
    public ResponseEntity<List<TeamResponse>> findMyTeams(Authentication authentication) {
        Long userId = currentUserId(authentication);
        List<TeamResponse> teams = teamService.findByUserId(userId).stream().map(mapper::toResponse).toList();
        return ResponseEntity.ok(teams);
    }

    @Override
    public ResponseEntity<TeamResponse> create(TeamRequest request, Authentication authentication) {
        Long userId = currentUserId(authentication);
        Team toCreate = mapper.toDomain(request).toBuilder().userId(userId).build();
        return ResponseEntity.status(201).body(mapper.toResponse(teamService.create(toCreate)));
    }

    @Override
    public ResponseEntity<String> analysis(Long id) {
        return ResponseEntity.ok(teamService.analyzeTeam(id));
    }

    @Override
    public ResponseEntity<TeamResponse> addPokemon(Long id, TeamPokemonRequest request) {
        return ResponseEntity.ok(mapper.toResponse(teamService.addPokemon(id, request.pokemonId())));
    }

    @Override
    public ResponseEntity<TeamResponse> removePokemon(Long id, Long pokemonId) {
        return ResponseEntity.ok(mapper.toResponse(teamService.removePokemon(id, pokemonId)));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        teamService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return user.getId();
    }
}
