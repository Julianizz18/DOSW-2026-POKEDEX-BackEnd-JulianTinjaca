package com.pokedex.pokedex_api.persistence.adapter;

import com.pokedex.pokedex_api.core.model.Favorite;
import com.pokedex.pokedex_api.core.port.FavoritePersistencePort;
import com.pokedex.pokedex_api.persistence.mapper.FavoritePersistenceMapper;
import com.pokedex.pokedex_api.persistence.repository.relational.FavoriteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FavoritePersistenceAdapter implements FavoritePersistencePort {

    private final FavoriteJpaRepository favoriteRepository;
    private final FavoritePersistenceMapper mapper;

    @Override
    public List<Favorite> findByUserId(Long userId) {
        return favoriteRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByUserIdAndPokemonId(Long userId, Long pokemonId) {
        return favoriteRepository.existsByUserIdAndPokemonId(userId, pokemonId);
    }

    @Override
    public Favorite save(Favorite favorite) {
        return mapper.toDomain(favoriteRepository.save(mapper.toEntity(favorite)));
    }

    @Override
    public void deleteByUserIdAndPokemonId(Long userId, Long pokemonId) {
        favoriteRepository.deleteByUserIdAndPokemonId(userId, pokemonId);
    }
}
