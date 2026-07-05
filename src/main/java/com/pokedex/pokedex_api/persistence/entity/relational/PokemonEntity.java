package com.pokedex.pokedex_api.persistence.entity.relational;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA de Pokemon. Vive SOLO en la capa persistence.
 * La capa core NO conoce esta clase — solo conoce core.model.Pokemon.
 */
@Entity
@Table(name = "pokemon", indexes = {
        @Index(name = "idx_pokemon_number", columnList = "national_number")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PokemonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "national_number", nullable = false, unique = true)
    private Integer nationalNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Integer generation;

    @Builder.Default
    @Column(name = "has_mega", nullable = false)
    private Boolean hasMega = false;

    // CORRECTO: FetchType.LAZY — se carga solo si se pide explícitamente (ver @EntityGraph, día miércoles/jueves)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pokemon_type",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    @Builder.Default
    private List<TypeEntity> types = new ArrayList<>();

    // CORRECTO: OneToOne LAZY — evita cargar stats innecesariamente en listados
    @OneToOne(mappedBy = "pokemon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PokemonStatsEntity stats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private RegionEntity region;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
