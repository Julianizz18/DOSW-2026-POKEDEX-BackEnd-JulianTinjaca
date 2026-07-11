# Arquitectura de Backend — Pokédex API

**Proyecto:** Pokédex — DOSW 2026 Intersemestral
**Autor:** Julian Tinjacá
**Última actualización:** 2026-07-10

## 1. Visión general

El backend es una API REST construida en **Java 21 + Spring Boot 3.5.6** que expone el catálogo de Pokémon, la gestión de usuarios/autenticación, favoritos y equipos competitivos. Sigue una **arquitectura hexagonal (puertos y adaptadores)** con separación estricta por capas:

```
controller  →  core (interfaces de servicio + puertos)  →  persistence (adapters)
```

`config` y `security` son transversales a las tres capas anteriores. El `core` no importa nada de Spring Data, JPA ni MongoDB — solo define contratos (`*PersistencePort`) que `persistence` implementa. Esto permite, en teoría, cambiar el motor de persistencia sin tocar la lógica de negocio.

Ver diagramas relacionados en `docs/diagramas-xml/`:
- `diagrama-clases.drawio` — modelo de dominio (core).
- `diagrama-componentes.drawio` — este mismo mapa de capas, en formato visual.
- `diagrama-contexto.drawio` — actores y sistemas externos.

## 2. Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje / runtime | Java 21 |
| Framework | Spring Boot 3.5.6 (Web, Validation) |
| Autenticación | Spring Security 6 + JWT (io.jsonwebtoken 0.12.6) + OAuth2 Client (Google) |
| Persistencia relacional | Spring Data JPA + PostgreSQL 15 (Hikari como pool de conexiones) |
| Control de esquema | Flyway (migraciones versionadas en `src/main/resources/db/migration`) |
| Persistencia no relacional | Spring Data MongoDB 7 (colección `pokemon_views`, reservada para estadísticas de uso — aún sin adapter conectado) |
| Mapeo entre capas | MapStruct 1.5.5 (genera los `*Impl` de los mappers en tiempo de compilación) |
| Documentación | springdoc-openapi 2.8.5 (Swagger UI) |
| Testing | JUnit 5, Mockito, AssertJ, Spring Security Test, JaCoCo 0.8.11 |
| Infraestructura local | Docker Compose (Postgres + Mongo) |

## 3. Estructura de paquetes

```
com.pokedex.pokedex_api
├── controller
│   ├── api/          interfaces @RequestMapping (contrato REST, documentado con @Operation)
│   ├── impl/          implementación de los controllers (AuthController, PokemonController, FavoriteController, TeamController)
│   ├── dto/request     records de entrada (validados con Bean Validation)
│   ├── dto/response    records de salida
│   ├── mapper/         MapStruct: DTO ↔ modelo de core
│   └── handler/        GlobalExceptionHandler (@RestControllerAdvice)
├── core
│   ├── model/           entidades de negocio inmutables (@Value/@Builder), sin anotaciones de framework
│   ├── port/            interfaces que persistence debe implementar (Inversión de Dependencias)
│   ├── service/interfaces   contratos de casos de uso
│   ├── service/impl         lógica de negocio
│   ├── validator/       reglas de negocio aisladas (p. ej. TeamValidator)
│   └── exception/       jerarquía de excepciones de negocio
├── persistence
│   ├── entity/relational   entidades JPA
│   ├── entity/document     documento Mongo (PokemonViewDocument)
│   ├── repository/         Spring Data JPA / Mongo repositories
│   ├── mapper/              MapStruct: entidad ↔ modelo de core
│   └── adapter/             implementan los *PersistencePort usando los repositories
├── security             JWT, filtro de autenticación, OAuth2, UserDetailsService
└── config                Swagger, habilitación de repositorios, BCrypt
```

## 4. Modelo de dominio (core)

| Clase | Tipo | Descripción |
|---|---|---|
| `Pokemon` | Value Object | id, número nacional, nombre, descripción, imagen, tipos, región, generación, mega evolución, stats |
| `PokemonStats` | Value Object | hp/attack/defense/specialAttack/specialDefense/speed + `getTotal()` calculado |
| `User` | Value Object | perfil de entrenador — el hash de contraseña vive aquí, pero la lógica de JWT vive en `security` |
| `Role` | Enum | `GUEST`, `TRAINER`, `ADMIN` |
| `Favorite` | Value Object | relación usuario–pokémon marcado como favorito (por id, no por referencia de objeto) |
| `Team` | Value Object | equipo de un entrenador, hasta 6 Pokémon (`pokemonIds`, validado por `TeamValidator`) |
| `PokemonFilterCriteria` | Record | filtros opcionales de búsqueda (tipo, región, generación, stats mínimos) |

Todos los modelos de `core` son inmutables (`@Value` de Lombok) y no tienen ninguna dependencia hacia JPA/Jackson — el acoplamiento a framework vive exclusivamente en `persistence.entity` y `controller.dto`.

## 5. Autenticación y autorización

Dos mecanismos conviven:

1. **Credenciales propias** (`POST /v1/auth/register`, `POST /v1/auth/login`): password hasheado con BCrypt, login valida contra `AuthenticationManager`, y se emite un JWT firmado con HMAC-SHA (`JwtService`).
2. **Google OAuth2** (`GET /oauth2/authorization/google`): delegado a Spring Security OAuth2 Client. Al éxito, `OAuth2SuccessHandler` busca o crea el `User` (rol `TRAINER` por defecto) y devuelve un JWT igual que el login clásico — **el frontend termina manejando un solo tipo de token sin importar el método de login**.

El login con Google **solo se activa si `GOOGLE_CLIENT_ID` es distinto del valor por defecto `CHANGE_ME`** (`SecurityConfig`), para que el proyecto pueda arrancar en desarrollo sin credenciales reales.

Todas las peticiones (salvo las públicas) pasan por `JwtAuthFilter` (`OncePerRequestFilter`), que:
1. Extrae el header `Authorization: Bearer <token>`.
2. Valida firma y expiración (`JwtService`).
3. Carga el `UserDetails` vía `UserDetailsServiceImpl` y lo coloca en el `SecurityContext`.

Autorización por rol declarada en `SecurityConfig` (`hasRole`/`hasAnyRole`) sobre patrones de ruta — ver tabla de endpoints abajo.

Sesión: `STATELESS` (sin `HttpSession`), CSRF deshabilitado (API pura, sin formularios server-side), CORS abierto a cualquier origen con credenciales habilitadas (`allowedOriginPatterns=*`).

## 6. Endpoints y control de acceso

Prefijo global: `/api` (`server.servlet.context-path`).

| Método | Ruta | Acceso | Controlador |
|---|---|---|---|
| POST | `/v1/auth/register` | público | AuthController |
| POST | `/v1/auth/login` | público | AuthController |
| GET | `/oauth2/authorization/google` | público | Spring Security (OAuth2) |
| GET | `/v1/pokemon`, `/{id}`, `/search`, `/filter` | público | PokemonController |
| POST/PUT/DELETE | `/v1/pokemon/**` | `ADMIN` | PokemonController |
| GET/POST/DELETE | `/v1/favorites/**` | `TRAINER`, `ADMIN` | FavoriteController |
| GET/POST/DELETE | `/v1/teams/**` | `TRAINER`, `ADMIN` | TeamController |
| GET | `/swagger-ui/**`, `/v3/api-docs/**` | público | springdoc |

> Nota: `SecurityConfig` también reserva `/v1/stats/**` para `TRAINER`/`ADMIN`, pero **ese controlador aún no existe** (la colección Mongo `pokemon_views` está definida pero sin adapter ni servicio conectado) — es config para un RF futuro.

## 7. Persistencia

- **PostgreSQL** guarda el catálogo (`pokemon`, `pokemon_stats`, `type`, `region`), usuarios (`app_user`) y datos de entrenador (`team`, `team_pokemon`, `favorite`). Esquema versionado con Flyway:
  - `V1__init_schema.sql`: DDL completo.
  - `V2__seed_admin.sql`: usuario admin de prueba (`admin@pokedex.com` / `Admin1234`).
  - `spring.jpa.hibernate.ddl-auto=validate` — Hibernate **no** genera ni altera el esquema en runtime, todo cambio de esquema debe ir en una migración nueva.
- **MongoDB** está preparado (`PokemonViewDocument`/`PokemonViewMongoRepository`) para estadísticas de uso del catálogo, pero **ningún componente lo usa todavía** — es infraestructura lista para un RF pendiente de implementar.
- Los adapters (`*PersistenceAdapter`) traducen entidad JPA ↔ modelo de core vía MapStruct y son el único lugar del código que conoce Spring Data.

## 8. Manejo de errores

`GlobalExceptionHandler` centraliza la traducción de excepciones a HTTP, devolviendo siempre el mismo contrato `ApiError` (status, code, message, path, timestamp, errores de campo):

| Excepción | HTTP | Code |
|---|---|---|
| `ResourceNotFoundException` | 404 | dinámico (`ex.getErrorCode()`) |
| `DuplicateResourceException` | 409 | dinámico |
| `InvalidOperationException` | 400 | dinámico |
| `BusinessException` | 400 | dinámico |
| `MethodArgumentNotValidException` (Bean Validation) | 400 | `VALIDATION_ERROR` |
| `BadCredentialsException` | 401 | `INVALID_CREDENTIALS` |
| `AccessDeniedException` | 403 | `ACCESS_DENIED` |
| `Exception` (catch-all) | 500 | `INTERNAL_ERROR` |

## 9. Testing y calidad

**Estado real (medido el 2026-07-11, con JaCoCo, BD levantada):**

| Métrica | Cobertura |
|---|---|
| Instrucciones | 98.2% |
| Líneas | 98.2% |
| Ramas | 82.0% |

141 tests (unitarios con Mockito para servicios/adapters/security/mappers, y `@WebMvcTest` para los 4 controllers), todos pasan. Cubren prácticamente toda la capa `core` (servicios, validator), `persistence` (adapters y mappers de MapStruct, probados con repositorios mockeados + el mapper real generado), `controller` (los 4 controllers vía slice tests) y `security` (JWT, filtro, OAuth2, UserDetailsService). El `GlobalExceptionHandler` también se prueba directamente, method por method.

Lo que queda sin cubrir (los 46 instructions / 22 branches restantes) son ramas menores de null-safety generadas por MapStruct y un par de caminos de `resolveRegion`/`resolveType` en `PokemonPersistenceAdapter` poco relevantes para el negocio — no justifican tests adicionales por ahora.

**Sigue faltando** un tipo de prueba distinto: pruebas de integración/end-to-end reales contra Postgres/Mongo (con `@SpringBootTest` + Testcontainers o la BD de docker-compose) que validen flujos completos (registro → login → crear Pokémon como admin → favorito → equipo). Las pruebas actuales son unitarias/de slice con mocks; dan cobertura de código pero no prueban la integración real entre capas (JPA, Flyway, Mongo).

El `pom.xml` ahora tiene el goal `check` de JaCoCo configurado con una regla `BUNDLE`/`INSTRUCTION`/`COVEREDRATIO` mínima de **0.70** — `mvn test` falla el build si la cobertura cae por debajo de ese umbral, así que el "mínimo 70%" del README ya es una garantía real, no solo aspiracional.

## 10. Despliegue local

```bash
docker-compose up -d                 # Postgres 5432 + Mongo 27017
$env:GOOGLE_CLIENT_ID = "..."        # solo si se quiere probar login con Google
$env:GOOGLE_CLIENT_SECRET = "..."
./mvnw.cmd spring-boot:run
```

- App: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- El JWT_SECRET y GOOGLE_CLIENT_* se inyectan por variable de entorno (`application.properties` trae valores por defecto solo para desarrollo — **deben cambiarse antes de cualquier despliegue real**).

## 11. Decisiones de diseño relevantes

- **`PasswordEncoderConfig` separado de `SecurityConfig`**: evita una dependencia circular, porque `SecurityConfig` depende de `OAuth2SuccessHandler`, que a su vez depende del bean `PasswordEncoder`.
- **Modelos de core sin anotaciones de persistencia**: permite testear servicios con Mockito sin levantar contexto de Spring ni base de datos.
- **JWT único para ambos métodos de login**: el frontend no necesita lógica distinta según si el usuario entró por credenciales o por Google.
- **`ddl-auto=validate`**: fuerza a que todo cambio de esquema pase por una migración Flyway explícita y versionada, evitando drift entre entornos.
