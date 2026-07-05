-- V2__seed_admin.sql
-- Usuario administrador de prueba para poder probar los endpoints
-- protegidos con hasRole('ADMIN') desde el arranque, ya que el registro
-- público (RF-01) siempre crea cuentas TRAINER (regla de negocio).
--
-- Credenciales de prueba:
--   email:    admin@pokedex.com
--   password: Admin1234
--
-- Cambia esta contraseña antes de desplegar a un entorno real.
INSERT INTO app_user (email, username, password_hash, role, enabled)
VALUES (
    'admin@pokedex.com',
    'admin',
    '$2b$10$FWTDpGvGLfcypgnClkirKOHwqgvkl2tccgYAhFczCwv/39Cvrjlau',
    'ADMIN',
    TRUE
);
