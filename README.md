# Pokédex API

**Autor:** Julian Tinjacá
**Asignatura:** Desarrollo y Operaciones de Software (DOSW) · 2026 Intersemestral

## Descripción

Pokédex es una API REST que permite a un entrenador explorar, buscar, filtrar y comparar Pokémon, gestionar equipos con análisis competitivo, marcar favoritos y consultar estadísticas de uso del catálogo. El sistema maneja tres roles — Visitante, Usuario estándar y Administrador — y separa autenticación por credenciales propias y por Gmail (OAuth2).

El backend está construido en **Java 21 + Spring Boot 3.3**, con una arquitectura por capas (`controller`, `core`, `persistence`, `config`, `security`) que aísla la lógica de negocio de los detalles de framework y persistencia. El catálogo de Pokémon vive en **PostgreSQL** (vía JPA/Flyway) y las estadísticas de uso en **MongoDB**.

## Stack tecnológico

| Tecnología | Uso |
|---|---|
| Java 21 / Spring Boot 3.3 | Framework principal |
| Spring Security 6 + JWT + OAuth2 | Autenticación y autorización |
| Spring Data JPA + PostgreSQL | Persistencia relacional (catálogo, usuarios, equipos) |
| Spring Data MongoDB | Persistencia no relacional (estadísticas y logs de consulta) |
| MapStruct | Mapeo entre capas (DTO ↔ Core ↔ Entidad) |
| Flyway | Control de versiones del esquema de base de datos |
| Springdoc OpenAPI (Swagger) | Documentación interactiva de la API |
| JUnit 5 + Mockito + JaCoCo | Pruebas unitarias y cobertura (mínimo 70%) |
| Docker Compose | Entorno local de PostgreSQL + MongoDB |

## Análisis de requerimientos

El detalle completo de los 23 requerimientos funcionales, los 8 requerimientos no funcionales (bajo el modelo ISO/IEC 25010) y las reglas de negocio está en el documento de análisis de requerimientos:

[Analisis_Requerimientos_Pokedex_DOSW.docx](docs/Analisis_Requerimientos_Pokedex_DOSW.docx)


## Arquitectura

El proyecto sigue una arquitectura por capas con inversión de dependencias: `controller` depende de interfaces de `core`, y `core` no conoce nada de `persistence` — solo define **puertos** que la capa `persistence` implementa mediante **adapters**.

```
controller → core (interfaces de servicio) → persistence (a través de adapters)
```

Las capas `config` y `security` son transversales a las tres anteriores.

## Diagramas

### 1. Diagrama de Casos de uso

![Caso de uso 1.png](docs/Casos%20de%20uso/Caso%20de%20uso%201.png)
![Caso de uso 2.png](docs/Casos%20de%20uso/Caso%20de%20uso%202.png)
![Caso de uso 3.png](docs/Casos%20de%20uso/Caso%20de%20uso%203.png)
![Caso de uso 4.png](docs/Casos%20de%20uso/Caso%20de%20uso%204.png)
![Caso de uso 5.png](docs/Casos%20de%20uso/Caso%20de%20uso%205.png)
![Caso de uso 6.png](docs/Casos%20de%20uso/Caso%20de%20uso%206.png)
![Caso de uso 7.png](docs/Casos%20de%20uso/Caso%20de%20uso%207.png)
![Caso de uso 8.png](docs/Casos%20de%20uso/Caso%20de%20uso%208.png)
![Caso de uso 9.png](docs/Casos%20de%20uso/Caso%20de%20uso%209.png)
![Caso de uso 10.png](docs/Casos%20de%20uso/Caso%20de%20uso%2010.png)
![Caso de uso 11.png](docs/Casos%20de%20uso/Caso%20de%20uso%2011.png)
![Caso de uso 12.png](docs/Casos%20de%20uso/Caso%20de%20uso%2012.png)
![Caso de uso 13.png](docs/Casos%20de%20uso/Caso%20de%20uso%2013.png)
![Caso de uso 14.png](docs/Casos%20de%20uso/Caso%20de%20uso%2014.png)
![Caso de uso 15.png](docs/Casos%20de%20uso/Caso%20de%20uso%2015.png)
![Caso de uso 16.png](docs/Casos%20de%20uso/Caso%20de%20uso%2016.png)
![Caso de uso 17.png](docs/Casos%20de%20uso/Caso%20de%20uso%2017.png)
![Caso de uso 18.png](docs/Casos%20de%20uso/Caso%20de%20uso%2018.png)
![Caso de uso 19.png](docs/Casos%20de%20uso/Caso%20de%20uso%2019.png)
![Caso de uso 20.png](docs/Casos%20de%20uso/Caso%20de%20uso%2020.png)
![Caso de uso 21.png](docs/Casos%20de%20uso/Caso%20de%20uso%2021.png)
![Caso de uso 22.png](docs/Casos%20de%20uso/Caso%20de%20uso%2022.png)
![Caso de uso 23.png](docs/Casos%20de%20uso/Caso%20de%20uso%2023.png)

### 2. Diagrama de clases — capa Core

![Diagrama de clases.png](docs/Diagrama%20de%20clases/Diagrama%20de%20clases.png)


### 4. Diagrama de contexto
