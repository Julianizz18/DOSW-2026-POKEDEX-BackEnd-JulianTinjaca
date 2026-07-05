package com.pokedex.pokedex_api.persistence.entity.relational;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String name;

    @ElementCollection
    @CollectionTable(name = "team_pokemon", joinColumns = @JoinColumn(name = "team_id"))
    @Column(name = "pokemon_id")
    @Builder.Default
    private List<Long> pokemonIds = new ArrayList<>();
}
