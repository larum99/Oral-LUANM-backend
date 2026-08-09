# Oral-LUANM Backend

Backend Spring Boot para ORAL LUANM. Expone la API REST del sistema odontologico y usa MySQL para desarrollo/produccion, H2 para pruebas automatizadas y Flyway para migraciones versionadas.

## Requisitos

- Java 21
- Docker y Docker Compose para MySQL local
- Gradle Wrapper incluido en el proyecto
- MySQL 8+ para desarrollo/produccion


## Arquitectura modular

El backend esta organizado como monolito modular por dominios:

```text
com.dental.clinic
├── config
├── shared
│   ├── exception
│   ├── response
│   ├── security
│   └── util
└── modules
    ├── auth
    │   ├── controller
    │   ├── dto
    │   ├── entity
    │   ├── mapper
    │   ├── repository
    │   ├── security
    │   └── service
    ├── patient
    │   ├── controller
    │   ├── dto
    │   ├── entity
    │   ├── mapper
    │   ├── repository
    │   └── service
    ├── doctor
    │   ├── controller
    │   ├── dto
    │   ├── entity
    │   ├── mapper
    │   ├── repository
    │   └── service
    └── appointment
        ├── controller
        ├── dto
        ├── entity
        ├── mapper
        ├── repository
        ├── service
        └── util
```

Regla de trabajo: cada feature nueva debe vivir dentro de su modulo. Solo configuracion transversal va en `config` y componentes reutilizables realmente compartidos van en `shared`.
## Perfiles

El backend usa perfiles Spring:

```text
dev   Perfil local por defecto. Usa MySQL local y variables con fallback seguro para desarrollo.
test  Perfil de pruebas. Usa H2 en memoria y JWT de test.
prod  Perfil de despliegue. Exige variables reales de DB y JWT, sin defaults sensibles.
```

Si no defines `SPRING_PROFILES_ACTIVE`, Spring usa `dev`.

## Configuracion local

1. Copiar variables de entorno:

```bash
cp .env.example .env
```

2. Editar `.env` y definir al menos:

```env
SPRING_PROFILES_ACTIVE=dev
JWT_SECRET=coloca-un-secreto-local-de-minimo-32-caracteres
JWT_EXPIRATION_MINUTES=480
APP_ADMIN_EMAIL=admin@admin.com
APP_ADMIN_PASSWORD=cambia-esta-clave-local
APP_CORS_ALLOWED_ORIGINS=http://localhost:5500,http://127.0.0.1:5500
```

`JWT_SECRET` es obligatorio y debe tener minimo 32 caracteres.

## Variables para produccion

Para desplegar usa `SPRING_PROFILES_ACTIVE=prod` y define estas variables sin valores de ejemplo:

```env
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
DB_URL=jdbc:mysql://<host>:3306/dental_clinic?useSSL=true&serverTimezone=America/Bogota
DB_USERNAME=<usuario_db>
DB_PASSWORD=<clave_db>
JWT_SECRET=<secreto_fuerte_minimo_32_caracteres>
JWT_EXPIRATION_MINUTES=480
APP_ADMIN_EMAIL=<admin_real>
APP_ADMIN_PASSWORD=<clave_temporal_fuerte>
APP_CORS_ALLOWED_ORIGINS=https://tu-dominio.com
```

Notas de seguridad y despliegue:

- No uses `root` en produccion.
- No subas `.env` al repositorio.
- Cambia `APP_ADMIN_PASSWORD` despues del primer ingreso.
- Mantén `JPA_SHOW_SQL=false` y `HIBERNATE_FORMAT_SQL=false` salvo depuracion local.
- En produccion Swagger/OpenAPI esta deshabilitado por defecto. Habilitalo solo si el entorno esta protegido.
- Si adoptas Flyway sobre una base existente, revisa `FLYWAY_BASELINE_ON_MIGRATE` y `FLYWAY_BASELINE_VERSION`.

## Base de datos con Docker

Levantar MySQL local:

```bash
docker compose up -d
```

El contenedor expone MySQL en `localhost:3306` y crea la base `dental_clinic`.

Scripts de inicializacion:

```text
src/main/resources/database/01_schema.sql
src/main/resources/database/02_data.sql
```
## Migraciones de base de datos

Flyway ejecuta migraciones desde:

```text
src/main/resources/db/migration
```

Migraciones actuales:

```text
V1__create_clinic_schema.sql
V3__add_appointment_optimistic_lock.sql
```

Los datos de desarrollo viven en `src/main/resources/db/dev-data/V2__seed_initial_data.sql` y solo se cargan con el perfil `dev`. Produccion no ejecuta datos demo. Los archivos en `src/main/resources/database` se conservan como referencia historica, pero el flujo recomendado para despliegue es Flyway.

En bases nuevas, Flyway crea el esquema desde V1. En bases existentes, `dev` permite baseline por defecto para facilitar adopcion local; en `prod`, `baseline-on-migrate` queda apagado salvo que lo habilites conscientemente.

## Ejecutar backend

Desde `Oral-LUANM-backend` en WSL/Linux:

```bash
export SPRING_PROFILES_ACTIVE=dev
export JWT_SECRET=coloca-un-secreto-local-de-minimo-32-caracteres
./gradlew bootRun
```

En PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
$env:JWT_SECRET="coloca-un-secreto-local-de-minimo-32-caracteres"
./gradlew bootRun
```

La API queda disponible en:

```text
http://localhost:8080/api
```

## Usuario inicial

Al iniciar el backend se crea el usuario administrador local si no existe. La contrasena no se sobrescribe en reinicios posteriores:

```text
usuario: valor de APP_ADMIN_EMAIL
clave: valor de APP_ADMIN_PASSWORD
rol: ADMIN
```

## Autenticacion

Login:

```http
POST /api/auth/login
```

Body:

```json
{
  "email": "admin@admin.com",
  "password": "<APP_ADMIN_PASSWORD>"
}
```

La respuesta incluye un token JWT. Para consumir endpoints protegidos se debe enviar:

```http
Authorization: Bearer <token>
```

## Observabilidad y documentacion tecnica

- Healthcheck: `GET /actuator/health`.
- Info endpoint: `GET /actuator/info`.
- OpenAPI JSON en desarrollo: `GET /v3/api-docs`.
- Swagger UI en desarrollo: `GET /swagger-ui.html`.
- Virtual threads habilitados con `spring.threads.virtual.enabled=true`.

## Seguridad aplicada

- JWT firmado con HS256 y secreto obligatorio de minimo 32 caracteres.
- El rol efectivo se toma desde base de datos en cada request, no desde el token.
- El `uid` del token debe coincidir con el usuario actual.
- Sesiones stateless.
- CORS configurable por perfil/entorno.
- Headers de seguridad: `X-Frame-Options`, `X-Content-Type-Options`, `Referrer-Policy`, `Permissions-Policy`, `Content-Security-Policy`, `Strict-Transport-Security`.
- Stacktraces y mensajes internos no se exponen por configuracion del servidor.
- Las citas usan optimistic locking con `@Version` para detectar actualizaciones concurrentes.
- La disponibilidad se refuerza con indices de busqueda por especialista, ventana de tiempo y estado.

## Features cubiertas

### Authentication

```text
POST /api/auth/login
POST /api/auth/register
POST /api/auth/password-reset/request
POST /api/auth/password-reset/confirm
```

### Users & Roles

```text
GET  /api/users
POST /api/users
PUT  /api/users/{id}
GET  /api/roles
GET  /api/permissions
GET  /api/role-permissions
PUT  /api/roles/{roleId}/permissions
```

### Patients

```text
POST   /api/patients
GET    /api/patients
GET    /api/patients/{id}
PUT    /api/patients/{id}
DELETE /api/patients/{id}
GET    /api/patients-directory
```

### Specialists & Services

```text
GET  /api/specialists
POST /api/specialists
PUT  /api/specialists/{id}
GET  /api/services
POST /api/services
PUT  /api/services/{id}
GET  /api/specialist-services
POST /api/specialist-services
```

### Schedules

```text
GET  /api/specialist-schedules
POST /api/specialist-schedules
```

### Appointments

```text
GET   /api/appointments
POST  /api/appointments
PATCH /api/appointments/{id}
PATCH /api/appointments/{id}/status
GET   /api/my-appointments
GET   /api/appointment-history
```

### Clinical History

```text
GET  /api/medical-histories
POST /api/medical-histories
GET  /api/clinical-evolutions
POST /api/clinical-evolutions
```

## Validacion

Ejecutar pruebas con Java 21 desde WSL/Linux:

```bash
SPRING_PROFILES_ACTIVE=test ./gradlew test --no-daemon --console=plain
```

Tambien funciona sin exportar variables porque las pruebas activan `test` con `@ActiveProfiles("test")`.

## Checklist de entrega

Antes de desplegar, ejecuta la checklist integral en ../docs/deployment-checklist.md.
