package com.pokedex.pokedex_api.persistence.adapter;

import com.pokedex.pokedex_api.core.model.Team;
import com.pokedex.pokedex_api.core.port.TeamPersistencePort;
import com.pokedex.pokedex_api.persistence.mapper.TeamPersistenceMapper;
import com.pokedex.pokedex_api.persistence.repository.relational.TeamJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TeamPersistenceAdapter implements TeamPersistencePort {

    private final TeamJpaRepository teamRepository;
    private final TeamPersistenceMapper mapper;

    @Override
    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Team> findByUserId(Long userId) {
        return teamRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Team save(Team team) {
        var entity = mapper.toEntity(team);
        if (team.getId() != null) {
            entity = entity.toBuilder().id(team.getId()).build();
        }
        return mapper.toDomain(teamRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        teamRepository.deleteById(id);
    }
}
