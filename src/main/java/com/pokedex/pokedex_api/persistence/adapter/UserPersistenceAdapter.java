package com.pokedex.pokedex_api.persistence.adapter;

import com.pokedex.pokedex_api.core.model.User;
import com.pokedex.pokedex_api.core.port.UserPersistencePort;
import com.pokedex.pokedex_api.persistence.mapper.UserPersistenceMapper;
import com.pokedex.pokedex_api.persistence.repository.relational.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {

    private final UserJpaRepository userRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        if (user.getId() != null) {
            entity = entity.toBuilder().id(user.getId()).build();
        }
        return mapper.toDomain(userRepository.save(entity));
    }
}
