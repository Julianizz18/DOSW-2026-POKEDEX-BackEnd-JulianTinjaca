package com.pokedex.pokedex_api.persistence.mapper;

import com.pokedex.pokedex_api.core.model.Pokemon;
import com.pokedex.pokedex_api.core.model.PokemonStats;
import com.pokedex.pokedex_api.persistence.entity.relational.PokemonEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.PokemonStatsEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.RegionEntity;
import com.pokedex.pokedex_api.persistence.entity.relational.TypeEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PokemonPersistenceMapperTest {

    private final PokemonPersistenceMapper mapper = new PokemonPersistenceMapperImpl();

    @Test
    void toDomain_mapsRegionAndTypeNames() {
        PokemonEntity entity = PokemonEntity.builder()
                .id(1L).nationalNumber(25).name("Pikachu").generation(1).hasMega(false)
                .region(RegionEntity.builder().id(1L).name("Kanto").build())
                .types(List.of(TypeEntity.builder().id(1L).name("Electric").build()))
                .stats(PokemonStatsEntity.builder().hp(35).attack(55).defense(40)
                        .specialAttack(50).specialDefense(50).speed(90).build())
                .build();

        Pokemon pokemon = mapper.toDomain(entity);

        assertThat(pokemon.getRegion()).isEqualTo("Kanto");
        assertThat(pokemon.getTypes()).containsExactly("Electric");
        assertThat(pokemon.getStats().getHp()).isEqualTo(35);
        assertThat(pokemon.getDescription()).isNull();
    }

    @Test
    void toDomain_withNullRegionAndTypes_doesNotFail() {
        PokemonEntity entity = PokemonEntity.builder()
                .id(2L).nationalNumber(1).name("Bulbasaur").generation(1).hasMega(false)
                .types(null).region(null)
                .build();

        Pokemon pokemon = mapper.toDomain(entity);

        assertThat(pokemon.getRegion()).isNull();
        assertThat(pokemon.getTypes()).isEmpty();
    }

    @Test
    void toDomain_entityNull_returnsNull() {
        assertThat(mapper.toDomain((PokemonEntity) null)).isNull();
    }

    @Test
    void toDomain_statsNull_returnsNull() {
        assertThat(mapper.toDomain((PokemonStatsEntity) null)).isNull();
    }

    @Test
    void toNewEntity_ignoresIdTypesRegionStats() {
        Pokemon pokemon = Pokemon.builder()
                .id(99L).nationalNumber(25).name("Pikachu").generation(1).hasMega(true)
                .stats(PokemonStats.builder().hp(35).attack(55).defense(40)
                        .specialAttack(50).specialDefense(50).speed(90).build())
                .build();

        PokemonEntity entity = mapper.toNewEntity(pokemon);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("Pikachu");
        assertThat(entity.getStats()).isNull();
    }

    @Test
    void toNewEntity_null_returnsNull() {
        assertThat(mapper.toNewEntity(null)).isNull();
    }
}
