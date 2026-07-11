package com.pokedex.pokedex_api.controller.impl;

import com.pokedex.pokedex_api.controller.dto.response.FavoriteResponse;
import com.pokedex.pokedex_api.controller.mapper.FavoriteDtoMapper;
import com.pokedex.pokedex_api.core.model.Favorite;
import com.pokedex.pokedex_api.core.model.Role;
import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.core.service.interfaces.FavoriteService;
import com.pokedex.pokedex_api.core.service.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FavoriteController.class,
        excludeAutoConfiguration = OAuth2ClientAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = {
                        "com.pokedex.pokedex_api.security..*",
                        "com.pokedex.pokedex_api.config..*"
                }))
@AutoConfigureMockMvc(addFilters = false)
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoriteService favoriteService;
    @MockBean
    private FavoriteDtoMapper mapper;
    @MockBean
    private UserService userService;

    private final UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken("ash@pokemon.com", null);

    @Test
    void findMyFavorites_returns200() throws Exception {
        User user = User.builder().id(1L).email("ash@pokemon.com").role(Role.TRAINER).build();
        Favorite favorite = Favorite.builder().id(1L).userId(1L).pokemonId(25L).addedAt(LocalDateTime.now()).build();
        FavoriteResponse response = new FavoriteResponse(1L, 1L, 25L, LocalDateTime.now());

        when(userService.findByEmail("ash@pokemon.com")).thenReturn(user);
        when(favoriteService.findByUserId(1L)).thenReturn(List.of(favorite));
        when(mapper.toResponse(favorite)).thenReturn(response);

        mockMvc.perform(get("/v1/favorites").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pokemonId").value(25L));
    }

    @Test
    void add_returns201() throws Exception {
        User user = User.builder().id(1L).email("ash@pokemon.com").role(Role.TRAINER).build();
        Favorite favorite = Favorite.builder().id(1L).userId(1L).pokemonId(25L).addedAt(LocalDateTime.now()).build();
        FavoriteResponse response = new FavoriteResponse(1L, 1L, 25L, LocalDateTime.now());

        when(userService.findByEmail("ash@pokemon.com")).thenReturn(user);
        when(favoriteService.add(1L, 25L)).thenReturn(favorite);
        when(mapper.toResponse(favorite)).thenReturn(response);

        mockMvc.perform(post("/v1/favorites/25").principal(auth))
                .andExpect(status().isCreated());
    }

    @Test
    void remove_returns204() throws Exception {
        User user = User.builder().id(1L).email("ash@pokemon.com").role(Role.TRAINER).build();
        when(userService.findByEmail("ash@pokemon.com")).thenReturn(user);

        mockMvc.perform(delete("/v1/favorites/25").principal(auth))
                .andExpect(status().isNoContent());
    }
}
