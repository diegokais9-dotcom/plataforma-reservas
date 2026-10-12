# Plataforma de Reservas y Servicios Hoteleros

Backend REST para la gestión de reservas de un hotel: autenticación segura con JWT, catálogo de habitaciones y servicios, y reservas con validación de disponibilidad por fechas.

Proyecto del curso **Desarrollo Web Integrado** (Universidad Tecnológica del Perú, sección 34380).

## Tecnologías

| Componente | Versión / herramienta |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Seguridad | Spring Security 7 + JWT (jjwt 0.11.5) + BCrypt |
| Persistencia | Spring Data JPA, Hibernate 7 |
| Base de datos | MySQL 8.0 |
| Utilidades | Lombok, Bean Validation (Jakarta) |
| Construcción | Maven (Maven Wrapper incluido) |
| Pruebas manuales | Postman |

## Arquitectura

El proyecto sigue una arquitectura por capas:

```
src/main/java/com/reservas/plataforma_reservas
├── config        Configuración de seguridad y datos iniciales
├── controller    Endpoints REST
├── dto           Objetos de transferencia de datos (con validaciones)
├── entity        Entidades JPA
├── repository    Acceso a datos (Spring Data JPA y consultas JPQL)
├── security      Filtro JWT, generación de tokens y UserDetails
└── service       Lógica de negocio
```

La inyección de dependencias se hace por constructor (`@RequiredArgsConstructor`) en los módulos de Servicio, Habitación y Reserva.

## Modelo de datos

- `usuarios` y `roles`: relación muchos a muchos mediante `usuario_rol`.
- `servicios`: servicios adicionales del hotel (desayuno, spa, etc.).
- `habitaciones`: número, tipo (`SIMPLE`, `DOBLE`, `SUITE`), capacidad, precio por noche y estado (`DISPONIBLE`, `OCUPADA`, `MANTENIMIENTO`).
- `reservas`: relación muchos a uno con `usuarios` y con `habitaciones`; guarda fechas, estado (`CONFIRMADA`, `CANCELADA`) y total.

## Cómo ejecutarlo

### Requisitos
- Java 21
- MySQL 8 en ejecución

### Pasos

1. Crear la base de datos:
```sql
   CREATE DATABASE plataforma_reservas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
2. Revisar `src/main/resources/application.yml` y ajustar la URL, el usuario y la contraseña de MySQL (por defecto puerto `3310`).
3. Ejecutar:
```
   ./mvnw spring-boot:run
```
   En Windows (PowerShell):
```
   .\mvnw.cmd spring-boot:run
```
4. La API queda disponible en `http://localhost:8080`.

Las tablas se crean automáticamente (`ddl-auto: update`). Al iniciar, la aplicación crea los roles `ROLE_ADMIN` y `ROLE_CLIENTE` y un usuario administrador de prueba.

### Usuario administrador de prueba

| Email | Contraseña |
|---|---|
| `admin@hotel.com` | `Admin1234` |

Son credenciales de demostración; deben cambiarse en cualquier entorno real.

## Seguridad

- Contraseñas cifradas con BCrypt.
- Autenticación stateless con JWT (validez de 24 horas).
- Todas las rutas, salvo registro y login, exigen el header:
```
  Authorization: Bearer <token>
```
- Todo usuario que se registra recibe el rol `ROLE_CLIENTE`.

## Endpoints

### Autenticación (públicos)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/auth/register` | Registrar un usuario |
| POST | `/api/auth/login` | Iniciar sesión y obtener el token |
| GET | `/api/auth/verificar` | Verificar que el token es válido (requiere token) |

### Servicios

| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/servicios` | Usuario autenticado |
| GET | `/api/servicios/buscar?nombre=` | Usuario autenticado |
| GET | `/api/servicios/{id}` | Usuario autenticado |
| POST | `/api/servicios` | Administrador |
| PUT | `/api/servicios/{id}` | Administrador |
| DELETE | `/api/servicios/{id}` | Administrador |

### Habitaciones

| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/habitaciones` | Usuario autenticado |
| GET | `/api/habitaciones/disponibles?capacidad=` | Usuario autenticado |
| GET | `/api/habitaciones/{id}` | Usuario autenticado |
| POST | `/api/habitaciones` | Administrador |
| PUT | `/api/habitaciones/{id}` | Administrador |
| DELETE | `/api/habitaciones/{id}` | Administrador |

### Reservas

| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/reservas` | Usuario autenticado |
| GET | `/api/reservas/mis-reservas` | Usuario autenticado |
| GET | `/api/reservas/{id}` | Dueño de la reserva |
| PUT | `/api/reservas/{id}/cancelar` | Dueño de la reserva |
| GET | `/api/reservas` | Administrador |

## Ejemplos

**Registro**
```json
{
  "nombre": "Diego",
  "email": "diego@correo.com",
  "password": "123456",
  "confirmPassword": "123456"
}
```

**Login**
```json
{
  "email": "diego@correo.com",
  "password": "123456"
}
```

**Crear una habitación (administrador)**
```json
{
  "numero": "101",
  "tipo": "DOBLE",
  "capacidad": 2,
  "precioNoche": 120.00
}
```

**Crear una reserva**
```json
{
  "habitacionId": 1,
  "fechaEntrada": "2026-10-20",
  "fechaSalida": "2026-10-23"
}
```

## Reglas de negocio

- El correo electrónico es único.
- Las dos contraseñas del registro deben coincidir.
- La fecha de salida debe ser posterior a la de entrada, y la entrada no puede ser pasada.
- No se puede reservar una habitación en mantenimiento.
- No se permiten dos reservas confirmadas de la misma habitación con fechas que se crucen (consulta JPQL `contarCruces`); el sistema responde `409 Conflict`.
- El total de la reserva se calcula como noches × precio por noche.
- Un cliente solo puede ver y cancelar sus propias reservas.
- Solo el administrador crea, edita o elimina servicios y habitaciones; un cliente recibe `403 Forbidden`.

## Pendientes

- Rol de recepcionista (check-in y check-out).
- Interfaz web que consuma esta API.

## Integrantes

- Contreras Mamani, Diego Andrés
- Hervis Alarcón, Rodrigo Orlando
- Tenorio Rojas, Kevin Antony
- Blanco Vilca, Luis Abner

**Docente:** Chavez Bedoya, Wenceslao Hoberth
