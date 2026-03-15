# API de Registro de Usuarios

API RESTful para registro y autenticación de usuarios construida con Spring Boot.

---

## Descripción general

La solución expone endpoints de autenticación para el registro y login de usuarios. Todos los endpoints aceptan y retornan JSON. Las respuestas siguen un formato estándar con `status`, `message` y `data`. Los errores también respetan esta estructura con `data: null`.

**Stack:**
- Java 21 + Spring Boot
- Spring Security + JWT
- H2 (base de datos en memoria)
- Hibernate / JPA
- Gradle

---

## Estructura del proyecto

La arquitectura está basada en **Feature-Based Architecture** — cada paquete representa un feature independiente con sus propias capas (controller, service, repository, dto, entity).

```
src/main/java/ey/daniel/spring_test/
│
├── auth/                        # Feature: Autenticación
│   ├── AuthController.java
│   ├── AuthService.java
│   ├── AuthRepository.java
│   ├── AuthEntity.java
│   ├── AuthDto.java
│   └── AuthErrorCode.java
│
├── user/                        # Feature: Usuario
│   ├── UserController.java
│   ├── UserService.java
│   ├── UserRepository.java
│   ├── UserEntity.java
│   ├── UserDto.java
│   ├── UserErrorCode.java
│   └── Phone.java
│
├── common/                      # Capa transversal
│   ├── exception/
│   │   ├── AppException.java
│   │   ├── BaseErrorCode.java
│   │   ├── ErrorCode.java
│   │   └── GlobalExceptionHandler.java
│   ├── response/
│   │   └── ApiResponse.java
│   └── util/
│       └── ResponseUtil.java
│
└── config/                      # Configuración
    ├── SecurityConfig.java
    └── JwtUtil.java
```

---

## Endpoints

### POST `/auth/signup`

Registra un nuevo usuario.

**Request:**
```json
{
  "name": "Juan Rodriguez",
  "email": "juan@rodriguez.org",
  "password": "Hunter22",
  "phones": [
    {
      "number": "1234567",
      "city_code": "1",
      "country_code": "57"
    }
  ]
}
```

**Validaciones:**
- `email` debe tener formato válido (ej: `usuario@dominio.cl`)
- `password` debe tener exactamente 1 mayúscula, al menos 1 minúscula y exactamente 2 números
- `phones` no puede estar vacío
- `name` es obligatorio

**Response exitosa (201):**
```json
{
  "status": 201,
  "message": "Created",
  "data": {
    "id": "3fb9e5ef-c0cf-4540-937f-87adc5754f41",
    "created": "2026-03-15T01:25:08.687817",
    "modified": "2026-03-15T01:25:08.687817",
    "last_login": "2026-03-15T01:25:08.730320",
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "isactive": true
  }
}
```

**Response de error (409):**
```json
{
  "status": 409,
  "message": "El correo ya registrado",
  "data": null
}
```

---

### POST `/auth/login`

Autentica un usuario existente y retorna un nuevo token.

**Request:**
```json
{
  "email": "juan@rodriguez.org",
  "password": "Hunter22"
}
```

**Response exitosa (200):**
```json
{
  "status": 200,
  "message": "OK",
  "data": {
    "id": "3fb9e5ef-c0cf-4540-937f-87adc5754f41",
    "created": "2026-03-15T01:25:08.687817",
    "modified": "2026-03-15T01:25:08.687817",
    "last_login": "2026-03-15T01:30:00.000000",
    "token": "eyJhbGciOiJIUzM4NCJ9...",
    "isactive": true
  }
}
```

**Response de error (401):**
```json
{
  "status": 401,
  "message": "Credenciales inválidas",
  "data": null
}
```

---

## Diagramas

### Diagrama ER

Muestra las tablas de la base de datos y sus relaciones.

![ER Diagram](diagrams/er-diagram.puml)

### Diagrama de Componentes

Muestra la interacción entre los features `auth`, `user` y la capa `common`.

![Component Diagram](diagrams/component-diagram.puml)

### Diagrama de Flujo

Muestra el flujo completo de las operaciones `signup` y `login`.

![Flow Diagram](diagrams/flow-diagram.puml)

---

## Cómo ejecutar

```bash
./gradlew bootRun
```

La aplicación corre en `http://localhost:8082`.

Para desarrollo con auto-reload, en dos terminales:

```bash
# Terminal 1
./gradlew bootRun

# Terminal 2
./gradlew compileJava --continuous
```

---

## Tests

```bash
./gradlew test
```
