package com.pokedex.pokedex_api.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokedex.pokedex_api.controller.dto.request.TeamPokemonRequest;
import com.pokedex.pokedex_api.controller.dto.request.TeamRequest;
import com.pokedex.pokedex_api.controller.dto.response.TeamResponse;
import com.pokedex.pokedex_api.controller.mapper.TeamDtoMapper;
import com.pokedex.pokedex_api.core.model.Role;
import com.pokedex.pokedex_api.core.model.Team;
import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.core.service.interfaces.TeamService;
import com.pokedex.pokedex_api.core.service.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TeamController.class,
        excludeAutoConfiguration = OAuth2ClientAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = {
                        "com.pokedex.pokedex_api.security..*",
                        "com.pokedex.pokedex_api.config..*"
                }))
@AutoConfigureMockMvc(addFilters = false)
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamService teamService;
    @MockBean
    private TeamDtoMapper mapper;
    @MockBean
    private UserService userService;

    private final UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken("ash@pokemon.com", null);

    @Test
    void findMyTeams_returns200() throws Exception {
        User user = User.builder().id(1L).email("ash@pokemon.com").role(Role.TRAINER).build();
        Team team = Team.builder().id(1L).userId(1L).name("Equipo").pokemonIds(List.of(25L)).build();
        TeamResponse response = new TeamResponse(1L, 1L, "Equipo", List.of(25L));

        when(userService.findByEmail("ash@pokemon.com")).thenReturn(user);
        when(teamService.findByUserId(1L)).thenReturn(List.of(team));
        when(mapper.toResponse(team)).thenReturn(response);

        mockMvc.perform(get("/v1/teams").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Equipo"));
    }

    @Test
    void create_returns201() throws Exception {
        User user = User.builder().id(1L).email("ash@pokemon.com").role(Role.TRAINER).build();
        TeamRequest request = new TeamRequest("Equipo", List.of());
        Team domain = Team.builder().name("Equipo").pokemonIds(List.of()).build();
        Team created = Team.builder().id(1L).userId(1L).name("Equipo").pokemonIds(List.of()).build();
        TeamResponse response = new TeamResponse(1L, 1L, "Equipo", List.of());

        when(userService.findByEmail("ash@pokemon.com")).thenReturn(user);
        when(mapper.toDomain(request)).thenReturn(domain);
        when(teamService.create(any())).thenReturn(created);
        when(mapper.toResponse(created)).thenReturn(response);

        mockMvc.perform(post("/v1/teams").principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void analysis_returns200() throws Exception {
        when(teamService.analyzeTeam(1L)).thenReturn("Equipo 'Equipo': 1 Pokémon, 1 tipos distintos (Electric).");

        mockMvc.perform(get("/v1/teams/1/analysis"))
                .andExpect(status().isOk());
    }

    @Test
    void addPokemon_returns200() throws Exception {
        TeamPokemonRequest request = new TeamPokemonRequest(6L);
        Team updated = Team.builder().id(1L).userId(1L).name("Equipo").pokemonIds(List.of(25L, 6L)).build();
        TeamResponse response = new TeamResponse(1L, 1L, "Equipo", List.of(25L, 6L));

        when(teamService.addPokemon(1L, 6L)).thenReturn(updated);
        when(mapper.toResponse(updated)).thenReturn(response);

        mockMvc.perform(post("/v1/teams/1/pokemon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void removePokemon_returns200() throws Exception {
        Team updated = Team.builder().id(1L).userId(1L).name("Equipo").pokemonIds(List.of()).build();
        TeamResponse response = new TeamResponse(1L, 1L, "Equipo", List.of());

        when(teamService.removePokemon(1L, 25L)).thenReturn(updated);
        when(mapper.toResponse(updated)).thenReturn(response);

        mockMvc.perform(delete("/v1/teams/1/pokemon/25"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/v1/teams/1"))
                .andExpect(status().isNoContent());
    }
}
