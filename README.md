# Pokédex — Análisis de Requerimientos

**DOSW · 2026 Intersemestral**

## 1. Introducción y contexto

Este documento contiene el análisis de requerimientos del proyecto Pokédex, desarrollado en la asignatura Desarrollo y Operaciones de Software (DOSW). El sistema es una API REST que permite a un entrenador explorar, consultar, comparar y organizar información de Pokémon, gestionar equipos con análisis competitivo, y llevar un registro de estadísticas de uso.

El proyecto reconoce tres actores: **Visitante** (no autenticado), **Usuario estándar** (autenticado) y **Administrador**.

Los requerimientos se dividen en dos categorías con naturaleza distinta. Los **Requerimientos Funcionales (RF)** describen capacidades del sistema: acciones concretas que un actor ejecuta y que producen un resultado observable, siempre expresables como un flujo con entradas y salidas. Los **Requerimientos No Funcionales (RNF)** describen cualidades del sistema: no añaden una capacidad nueva, sino que imponen un umbral medible sobre cómo se comportan las capacidades que ya existen (tiempos de respuesta, disponibilidad, seguridad, mantenibilidad). Un RF se prueba ejecutando un caso de uso paso a paso; un RNF se prueba midiendo un número contra un umbral.

Esta distinción es la base de la sección 5: cada RNF de este documento incluye un criterio de aceptación medible, y ninguna funcionalidad (como un método de autenticación específico) aparece clasificada como cualidad de calidad.

## 2. Alcance

- Gestión de usuarios: registro, autenticación (credenciales propias y Gmail vía OAuth), administración de perfiles.
- Catálogo de Pokémon: operaciones CRUD reservadas al administrador, consulta pública para todos los actores.
- Exploración del catálogo: listado paginado, búsqueda, filtros, ordenamiento y detalle.
- Personalización: favoritos, equipos Pokémon con análisis competitivo, estadísticas de uso.
- Funcionalidad adicional: comparador de Pokémon.

## 3. Roles del sistema

| Rol | Permisos |
|---|---|
| Visitante | Listar, buscar, filtrar, ordenar, consultar detalle y comparar Pokémon. Registrarse e iniciar sesión. |
| Usuario estándar | Todo lo del Visitante, más: favoritos, equipos propios (crear, editar, eliminar, consultar análisis), estadísticas de uso. |
| Administrador | Todo lo del Usuario estándar, más: CRUD completo de Pokémon, administración de perfiles de otros usuarios. |

## 4. Requerimientos Funcionales

El catálogo consta de 23 requerimientos funcionales. Cada uno representa una única acción atómica: cuando una operación agrupaba lectura, creación, edición y borrado en un solo requerimiento (como ocurría con equipos y usuarios), se separó en requerimientos independientes — el mismo criterio que ya rige el CRUD de Pokémon.

| Código | Nombre | Actor principal |
|---|---|---|
| RF-01 | Registro de usuario | Visitante |
| RF-02 | Inicio de sesión con credenciales | Usuario registrado |
| RF-03 | Inicio de sesión con Gmail (OAuth) | Usuario registrado |
| RF-04 | Crear Pokémon | Administrador |
| RF-05 | Actualizar Pokémon | Administrador |
| RF-06 | Eliminar Pokémon | Administrador |
| RF-07 | Listar Pokémon (paginado) | Usuario / Visitante |
| RF-08 | Buscar Pokémon por nombre o número | Usuario / Visitante |
| RF-09 | Filtrar Pokémon | Usuario / Visitante |
| RF-10 | Ordenar listado de Pokémon | Usuario / Visitante |
| RF-11 | Consultar detalle de un Pokémon | Usuario / Visitante |
| RF-12 | Marcar o desmarcar Pokémon como favorito | Usuario registrado |
| RF-13 | Consultar lista de favoritos | Usuario registrado |
| RF-14 | Crear equipo Pokémon | Usuario registrado |
| RF-15 | Consultar análisis competitivo de un equipo | Usuario registrado |
| RF-16 | Editar composición de un equipo | Usuario registrado |
| RF-17 | Eliminar equipo | Usuario registrado |
| RF-18 | Consultar mis equipos | Usuario registrado |
| RF-19 | Consultar estadísticas de uso | Usuario / Administrador |
| RF-20 | Consultar listado de usuarios | Administrador |
| RF-21 | Cambiar rol o estado de un usuario | Administrador |
| RF-22 | Eliminar cuenta de usuario | Administrador |
| RF-23 | Comparador de Pokémon | Usuario / Visitante |

### 4.1 RF-01 — Registro de usuario

Permite a un visitante crear un perfil de entrenador para acceder a las funcionalidades que requieren autenticación. El sistema valida el formato de los datos, verifica que el correo no exista (RN-01) y crea la cuenta con rol de usuario estándar.

**Precondiciones:** no debe existir sesión activa; el correo no debe estar registrado previamente.

**Entrada:** nombre de entrenador (3–30 caracteres, único), correo electrónico (formato válido, único), contraseña (mínimo 8 caracteres, 1 mayúscula, 1 número — RN-10), confirmación de contraseña, avatar (opcional).

**Salida:** confirmación de registro, perfil de usuario creado (id, nombre, correo, rol), mensaje de error si el correo ya existe o algún campo es inválido.

**Flujo básico:** el visitante diligencia el formulario y confirma → el sistema valida formato y unicidad del correo → crea el perfil con rol estándar → confirma y redirige al inicio de sesión.

**Excepciones:** correo ya registrado → informa el conflicto; campo inválido → resalta el campo y el motivo.

### 4.2 RF-02 — Inicio de sesión con credenciales

Permite a un usuario registrado autenticarse mediante correo y contraseña.

**Precondiciones:** cuenta previamente creada; sin sesión activa.

**Entrada:** correo electrónico, contraseña.

**Salida:** sesión activa (token con id de usuario y rol), vista principal según rol, mensaje de error genérico ante fallo (sin revelar si el correo existe).

**Flujo básico:** el usuario ingresa correo y contraseña → el sistema valida las credenciales → crea la sesión → redirige a la vista correspondiente al rol.

**Excepciones:** credenciales incorrectas → mensaje genérico de error, sin indicar cuál campo falló.

### 4.3 RF-03 — Inicio de sesión con Gmail (OAuth)

Permite autenticarse mediante OAuth de Google en lugar de credenciales propias. Se trata como requerimiento independiente de RF-02 porque el actor invoca un flujo técnico distinto (token de terceros en vez de credenciales propias), con sus propias excepciones.

**Precondiciones:** cuenta previamente asociada a ese correo de Gmail; sin sesión activa.

**Entrada:** token OAuth de Gmail (solo se acepta este proveedor — RN-03).

**Salida:** sesión activa, vista principal según rol, mensaje de error si el token no es válido.

**Flujo básico:** el usuario selecciona "Iniciar con Gmail" → el sistema redirige al flujo OAuth de Google → Google retorna el token → el sistema valida el token y localiza la cuenta asociada → crea la sesión.

**Excepciones:** el proveedor del token no es Gmail → rechaza el acceso; no existe cuenta asociada a ese correo → ofrece registrarse.

### 4.4 RF-04 — Crear Pokémon

Permite al administrador registrar un nuevo Pokémon con toda su información asociada.

**Precondiciones:** sesión activa con rol administrador (RN-02).

**Entrada:** número de Pokédex (único e inmutable — RN-06), nombre, tipo primario (obligatorio) y secundario (opcional), región, generación, estadísticas base (no negativas), habilidades/movimientos (opcional), indicador de mega evolución, imagen (opcional).

**Salida:** Pokémon creado, confirmación, mensaje de error ante datos inválidos o número duplicado.

**Flujo básico:** el administrador diligencia el formulario y guarda → el sistema valida los datos y la unicidad del número de Pokédex → almacena el registro → confirma.

**Excepciones:** número de Pokédex duplicado → informa el conflicto; datos obligatorios faltantes o inválidos → resalta los campos.

### 4.5 RF-05 — Actualizar Pokémon

Permite modificar la información de un Pokémon existente, excepto su número de Pokédex (inmutable — RN-06).

**Precondiciones:** rol administrador; el Pokémon debe existir.

**Entrada:** número de Pokédex del registro a editar, campos editables (nombre, tipos, región, stats, habilidades, etc.).

**Salida:** Pokémon actualizado, confirmación, mensaje de error ante datos inválidos.

**Flujo básico:** el administrador localiza el Pokémon → el sistema precarga el formulario con los datos actuales → el administrador modifica y guarda → el sistema valida y actualiza el registro.

**Excepciones:** el Pokémon no existe → informa y cancela; datos inválidos → resalta los campos y no guarda.

### 4.6 RF-06 — Eliminar Pokémon

Permite eliminar un Pokémon del catálogo, controlando el caso en que esté referenciado por equipos o favoritos (RN-07).

**Precondiciones:** rol administrador; el Pokémon debe existir.

**Entrada:** número de Pokédex, confirmación explícita de la acción.

**Salida:** resultado de la eliminación, catálogo actualizado.

**Flujo básico:** el administrador selecciona el Pokémon y confirma → el sistema verifica dependencias en equipos y favoritos → elimina el registro y actualiza el catálogo.

**Excepciones:** el Pokémon está referenciado en equipos o favoritos → advierte y solicita confirmación adicional (RN-07).

### 4.7 RF-07 — Listar Pokémon (paginado)

Muestra el catálogo con información resumida (número, nombre, tipos, imagen), soportando paginación.

**Precondiciones:** debe existir al menos un Pokémon en el catálogo.

**Entrada:** página o desplazamiento (opcional, por defecto la primera), cantidad por página (opcional, valor por defecto del sistema).

**Salida:** listado ordenado por número de Pokédex, indicadores de paginación, mensaje si el catálogo está vacío.

**Flujo básico:** el actor ingresa a la sección principal → el sistema consulta el catálogo → muestra el listado paginado.

**Excepciones:** catálogo vacío → mensaje informativo en lugar del listado.

### 4.8 RF-08 — Buscar Pokémon por nombre o número

Permite localizar un Pokémon específico por coincidencia de nombre (parcial) o número exacto de Pokédex.

**Precondiciones:** debe existir al menos un Pokémon en el catálogo.

**Entrada:** término de búsqueda (texto o número, no vacío).

**Salida:** resultados coincidentes, mensaje si no hay coincidencias.

**Flujo básico:** el actor ingresa el término → el sistema consulta el catálogo → muestra las coincidencias.

**Excepciones:** sin coincidencias → mensaje sugiriendo revisar el término.

### 4.9 RF-09 — Filtrar Pokémon

Permite reducir el listado según uno o varios criterios combinables: región, tipo primario/secundario, generación, evolución, rango de estadísticas, habilidad o ataque aprendido, presencia de mega evolución, y un filtro adicional definido por el equipo de desarrollo (por ejemplo, color predominante).

**Precondiciones:** debe existir al menos un Pokémon en el catálogo.

**Entrada:** uno o más criterios de filtro (todos opcionales, combinables entre sí).

**Salida:** listado filtrado, mensaje si ningún registro cumple los criterios.

**Flujo básico:** el actor selecciona uno o varios criterios → el sistema aplica los filtros combinados sobre el catálogo → actualiza el listado.

**Excepciones:** ningún resultado cumple los criterios → mensaje y opción de limpiar filtros.

### 4.10 RF-10 — Ordenar listado de Pokémon

Permite reorganizar el listado por nombre, número de Pokédex o estadísticas, en orden ascendente o descendente. Se trata como requerimiento independiente de RF-09 porque ordenar no reduce el conjunto de resultados — opera sobre el listado ya filtrado o sobre el catálogo completo, y puede usarse sin aplicar ningún filtro.

**Entrada:** criterio de ordenamiento, dirección (ascendente/descendente).

**Salida:** listado reordenado.

**Flujo básico:** el actor selecciona un criterio de orden → el sistema reordena el listado actual → lo muestra actualizado.

### 4.11 RF-11 — Consultar detalle de un Pokémon

Muestra la información completa de un Pokémon: número, nombre, tipos, región, generación, estadísticas, habilidades, cadena de evolución, mega evolución e imagen. Cada consulta incrementa el contador interno de vistas usado en RF-19.

**Precondiciones:** el Pokémon debe existir.

**Entrada:** identificador del Pokémon.

**Salida:** vista de detalle completa, registro interno del evento de consulta.

**Flujo básico:** el actor selecciona un Pokémon → el sistema recupera su información completa → la muestra → registra el evento de consulta.

**Excepciones:** el Pokémon no existe o fue eliminado → informa y regresa al listado.

### 4.12 RF-12 — Marcar o desmarcar Pokémon como favorito

Permite a un usuario autenticado alternar el estado de favorito de un Pokémon.

**Precondiciones:** sesión activa; el Pokémon debe existir.

**Entrada:** identificador del Pokémon, acción (marcar/desmarcar).

**Salida:** confirmación visual del nuevo estado.

**Flujo básico:** el usuario selecciona el ícono de favorito → el sistema verifica la sesión → agrega o quita el Pokémon de la lista personal → actualiza el indicador.

**Excepciones:** sin sesión activa → solicita iniciar sesión o registrarse.

### 4.13 RF-13 — Consultar lista de favoritos

Muestra los Pokémon marcados como favoritos por el usuario autenticado. Se trata como requerimiento independiente de RF-12 porque es una operación de lectura pura, sin las entradas ni validaciones de la operación de escritura.

**Precondiciones:** sesión activa.

**Salida:** lista de Pokémon favoritos del usuario, mensaje si la lista está vacía.

**Flujo básico:** el usuario accede a su sección de favoritos → el sistema recupera la lista asociada a su cuenta → la muestra.

### 4.14 RF-14 — Crear equipo Pokémon

Permite crear un equipo nuevo, asignarle un nombre y, opcionalmente, agregar Pokémon iniciales (máximo 6 — RN-04).

**Precondiciones:** sesión activa; deben existir Pokémon en el catálogo.

**Entrada:** nombre del equipo (no vacío, único por usuario — RN-11), lista inicial de Pokémon (0 a 6).

**Salida:** equipo creado y asociado al usuario.

**Flujo básico:** el usuario asigna un nombre y, opcionalmente, agrega Pokémon iniciales → el sistema valida el límite (RN-04) y la unicidad del nombre (RN-11) → almacena el equipo.

**Excepciones:** se supera el límite de 6 → impide la acción e informa; nombre de equipo repetido para ese usuario → informa el conflicto.

### 4.15 RF-15 — Consultar análisis competitivo de un equipo

Calcula y muestra la cobertura de tipos, fortalezas, debilidades y balance de estadísticas de un equipo existente. Se separa de RF-14 porque el análisis se puede solicitar en cualquier momento sobre un equipo ya creado, no únicamente durante su creación.

**Precondiciones:** el equipo debe existir y pertenecer al usuario.

**Entrada:** identificador del equipo.

**Salida:** reporte de análisis competitivo (cobertura de tipos, debilidades, balance).

**Flujo básico:** el usuario solicita el análisis de un equipo propio → el sistema calcula el reporte a partir de los tipos y estadísticas de los Pokémon del equipo → lo muestra.

### 4.16 RF-16 — Editar composición de un equipo

Permite agregar o quitar Pokémon de un equipo ya existente.

**Precondiciones:** el equipo debe existir y pertenecer al usuario autenticado.

**Entrada:** identificador del equipo, identificador del Pokémon, acción (agregar/quitar).

**Salida:** equipo con su composición actualizada y análisis competitivo recalculado.

**Flujo básico:** el usuario selecciona un equipo propio y una acción → el sistema valida pertenencia del equipo → si es "agregar", valida el límite de 6 (RN-04) y que el Pokémon no esté repetido → actualiza el equipo y recalcula el análisis.

**Excepciones:** el equipo no pertenece al usuario → rechaza la operación; se supera el límite de 6 → informa y no guarda.

### 4.17 RF-17 — Eliminar equipo

Permite el borrado permanente de un equipo propio.

**Precondiciones:** el equipo debe existir y pertenecer al usuario.

**Entrada:** identificador del equipo, confirmación.

**Salida:** confirmación de eliminación, listado de equipos actualizado.

**Flujo básico:** el usuario selecciona un equipo propio y confirma la eliminación → el sistema borra el registro y actualiza el listado.

### 4.18 RF-18 — Consultar mis equipos

Muestra el listado de equipos asociados al usuario autenticado (RN-05).

**Precondiciones:** sesión activa.

**Salida:** listado de equipos propios con su información resumida.

**Flujo básico:** el usuario ingresa a la sección "Mis equipos" → el sistema consulta los equipos asociados a su cuenta → los muestra.

### 4.19 RF-19 — Consultar estadísticas de uso

Muestra indicadores de uso del catálogo: tasa de elección de Pokémon en equipos, cantidad de consultas de detalle, y rankings derivados. Visible tanto para usuarios estándar como administradores (RN-08).

**Precondiciones:** debe existir información de uso registrada.

**Entrada:** indicador a consultar (opcional), filtro por tipo, generación o periodo (opcional).

**Salida:** reporte de estadísticas, mensaje si no hay datos suficientes.

**Flujo básico:** el actor accede a la sección de estadísticas → el sistema calcula los indicadores a partir de los eventos de consulta (RF-11) y de elección en equipos (RF-14/RF-16) → los presenta.

**Excepciones:** sin datos suficientes → informa el estado vacío.

### 4.20 RF-20 — Consultar listado de usuarios

Muestra el listado de usuarios registrados con su rol y estado. Es una operación de solo lectura, separada de las acciones de escritura (RF-21, RF-22) porque no requiere el mismo nivel de confirmación ni auditoría.

**Precondiciones:** rol administrador (RN-09).

**Salida:** listado de usuarios con rol y estado, visible solo para administradores.

**Flujo básico:** el administrador accede a la gestión de usuarios → el sistema consulta y muestra el listado completo.

### 4.21 RF-21 — Cambiar rol o estado de un usuario

Permite a un administrador modificar el rol o habilitar/deshabilitar la cuenta de otro usuario.

**Precondiciones:** rol administrador; el usuario objetivo debe existir.

**Entrada:** identificador del usuario, nuevo rol o nuevo estado.

**Salida:** confirmación del cambio aplicado.

**Flujo básico:** el administrador selecciona un usuario y una acción → el sistema valida permisos → aplica el cambio → confirma.

**Excepciones:** la acción comprometería la última cuenta administradora del sistema → la impide e informa.

### 4.22 RF-22 — Eliminar cuenta de usuario

Permite a un administrador eliminar permanentemente la cuenta de otro usuario. Se separa de RF-21 por ser la acción más destructiva del grupo, con su propia confirmación explícita.

**Precondiciones:** rol administrador; el usuario objetivo debe existir; el objetivo no puede ser el propio administrador que ejecuta la acción (RN-12).

**Entrada:** identificador del usuario, confirmación explícita.

**Salida:** confirmación de la eliminación.

**Flujo básico:** el administrador selecciona un usuario y confirma la eliminación → el sistema valida que no sea la última cuenta administradora ni la cuenta propia (RN-12) → elimina la cuenta.

**Excepciones:** se intenta eliminar la última cuenta administradora o la cuenta propia → la operación se rechaza.

### 4.23 RF-23 — Comparador de Pokémon

Funcionalidad adicional que permite seleccionar entre 2 y 4 Pokémon y compararlos lado a lado por estadísticas, tipos, habilidades y debilidades.

**Precondiciones:** deben existir al menos dos Pokémon en el catálogo.

**Entrada:** conjunto de Pokémon a comparar (entre 2 y 4).

**Salida:** vista comparativa con las diferencias resaltadas.

**Flujo básico:** el actor selecciona "Comparar" y elige los Pokémon → el sistema recupera su información → muestra la comparación lado a lado.

**Excepciones:** se seleccionan menos de dos Pokémon → solicita completar la selección.

## 5. Requerimientos No Funcionales

Los requerimientos no funcionales se organizan según el modelo de características de calidad ISO/IEC 25010, el estándar de referencia para clasificar atributos de calidad de software. Cada uno incluye un criterio de aceptación medible, de modo que se puede convertir directamente en una prueba y no queda como una intención sin verificar.

### RNF-01 — Usabilidad

La interfaz permite llegar al detalle de un Pokémon en un máximo de 3 interacciones desde el listado. Los mensajes de error se presentan en español, sin jerga técnica, indicando claramente la causa del problema.

### RNF-02 — Accesibilidad

Contraste mínimo de nivel AA según WCAG 2.1, navegación completa por teclado sin necesidad de mouse, y texto alternativo en el 100% de las imágenes de Pokémon. Dentro de ISO 25010 esta característica es una subcaracterística de Usabilidad, pero se mantiene como sección independiente por su relevancia propia dentro del proyecto.

### RNF-03 — Rendimiento y escalabilidad

El listado de Pokémon carga en menos de 2 segundos y la búsqueda responde en menos de 1 segundo, bajo condiciones normales de red. El sistema soporta al menos 100 usuarios concurrentes sin que el tiempo de respuesta se degrade más de un 20% respecto a condiciones de baja carga. El catálogo se sirve con paginación o carga progresiva.

### RNF-04 — Compatibilidad

La aplicación funciona correctamente en las dos versiones estables más recientes de Chrome, Firefox, Edge y Safari, con diseño responsive verificado en resoluciones de escritorio (≥1280px), tableta (≥768px) y móvil (≥360px).

### RNF-05 — Confiabilidad y disponibilidad

Disponibilidad objetivo del 99% mensual. Ante la caída parcial de un componente de infraestructura (por ejemplo, el servicio de estadísticas en MongoDB), el catálogo principal en PostgreSQL sigue disponible: el sistema degrada funcionalidades de forma controlada en vez de fallar por completo.

### RNF-06 — Seguridad

Las contraseñas se almacenan cifradas con hash y salt (BCrypt). Toda la comunicación se realiza sobre HTTPS/TLS. Los tokens JWT están firmados (HS256/RS256) y expiran en un máximo de 24 horas. El control de acceso por rol se valida en cada endpoint del backend, no únicamente ocultando opciones en la interfaz. El análisis estático de seguridad no reporta vulnerabilidades críticas del OWASP Top 10.

### RNF-07 — Mantenibilidad

El código sigue una arquitectura modular por capas (controller, core, persistence, config, security) sin dependencias cruzadas entre capas no adyacentes. La cobertura de pruebas unitarias es de al menos 70% (medida con JaCoCo). El análisis estático (Checkstyle/SonarLint) no reporta errores críticos. Cada commit referencia un issue de Jira, garantizando trazabilidad entre requerimientos, historias y código.

### RNF-08 — Portabilidad

El entorno completo del sistema (API, PostgreSQL, MongoDB) puede desplegarse en cualquier máquina con Docker instalado mediante `docker-compose up`, sin configuración manual adicional más allá de variables de entorno (por ejemplo, `JWT_SECRET`).

## 6. Reglas de negocio

| No. | Descripción |
|---|---|
| RN-01 | El correo electrónico debe ser único por usuario; no pueden existir dos cuentas con el mismo correo. |
| RN-02 | Solo los usuarios con rol administrador pueden crear, actualizar o eliminar Pokémon del catálogo. |
| RN-03 | El inicio de sesión mediante proveedor externo solo se permite con cuentas de Gmail. |
| RN-04 | Un equipo Pokémon puede contener un máximo de 6 Pokémon. |
| RN-05 | Un usuario puede tener uno o más equipos Pokémon asociados a su perfil. |
| RN-06 | El número de Pokédex de un Pokémon es único e inmutable una vez creado el registro. |
| RN-07 | Al eliminar un Pokémon incluido en equipos o favoritos, el sistema advierte al administrador antes de confirmar la baja. |
| RN-08 | Las estadísticas de uso son visibles tanto para usuarios estándar como para administradores. |
| RN-09 | Solo los administradores pueden administrar (consultar, cambiar rol o estado) los perfiles de otros usuarios. |
| RN-10 | Las contraseñas deben tener mínimo 8 caracteres, con al menos una letra mayúscula y un número. |
| RN-11 | El nombre de un equipo debe ser único por usuario (no entre usuarios distintos). |
| RN-12 | Un administrador no puede eliminar ni deshabilitar su propia cuenta desde el panel de administración. |

## 7. Abreviaturas

| Abreviatura | Significado |
|---|---|
| DOSW | Desarrollo y Operaciones de Software |
| RF | Requerimiento Funcional |
| RNF | Requerimiento No Funcional |
| RN | Regla de Negocio |
| CRUD | Create, Read, Update, Delete |
| OAuth | Open Authorization |
| JWT | JSON Web Token |
| WCAG | Web Content Accessibility Guidelines |
| ISO/IEC 25010 | Estándar internacional de modelo de calidad de producto de software |
