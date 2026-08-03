# Oral-LUANM Backend

Backend Spring Boot para ORAL LUANM. Expone la API REST del sistema odontologico y usa MySQL como base de datos local.

## Requisitos

- Java 21
- Docker y Docker Compose
- Gradle Wrapper incluido en el proyecto

## Configuracion local

1. Copiar variables de entorno:

```bash
cp .env.example .env
```

2. Editar `.env` y definir un secreto JWT local:

```env
JWT_SECRET=coloca-un-secreto-local-de-minimo-32-caracteres
JWT_EXPIRATION_MINUTES=480
```

`JWT_SECRET` es obligatorio para levantar el backend.

## Base de datos con Docker

Levantar MySQL:

```bash
docker compose up -d
```

El contenedor expone MySQL en:

```text
localhost:3306
```

La base creada es:

```text
dental_clinic
```

Los scripts de inicializacion se ejecutan desde:

```text
src/main/resources/database/01_schema.sql
src/main/resources/database/02_data.sql
```

## Ejecutar backend

Desde la carpeta `Oral-LUANM-backend`:

```bash
export JWT_SECRET=coloca-un-secreto-local-de-minimo-32-caracteres
export JWT_EXPIRATION_MINUTES=480
./gradlew bootRun
```

En PowerShell:

```powershell
$env:JWT_SECRET="coloca-un-secreto-local-de-minimo-32-caracteres"
$env:JWT_EXPIRATION_MINUTES="480"
./gradlew bootRun
```

La API queda disponible en:

```text
http://localhost:8080/api
```

## Usuario inicial

Al iniciar el backend se crea o actualiza este usuario administrador local:

```text
usuario: admin@admin.com
clave: 12345678
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
  "password": "12345678"
}
```

La respuesta incluye un token JWT. Para consumir endpoints protegidos se debe enviar:

```http
Authorization: Bearer <token>
```

## Features cubiertas

### Feature transversal — Authentication

Tablas:

```text
users
roles
password_resets
```

Endpoints principales:

```text
POST /api/auth/login
POST /api/auth/register
POST /api/auth/password-reset/request
POST /api/auth/password-reset/confirm
```

### Feature 1 — Users & Roles

Tablas:

```text
users
roles
permissions
role_permissions
```

Endpoints principales:

```text
GET  /api/users
POST /api/users
PUT  /api/users/{id}
GET  /api/roles
GET  /api/permissions
GET  /api/role-permissions
PUT  /api/roles/{roleId}/permissions
```

### Feature 2 — Patients

Tabla:

```text
patients
```

Endpoints principales:

```text
POST   /api/patients
GET    /api/patients
GET    /api/patients/{id}
PUT    /api/patients/{id}
DELETE /api/patients/{id}
GET    /api/patients-directory
```

### Feature 3 — Specialists & Services

Tablas:

```text
specialists
services
specialist_services
```

Endpoints principales:

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

### Feature 4 — Schedules

Tabla:

```text
specialist_schedules
```

Endpoints principales:

```text
GET  /api/specialist-schedules
POST /api/specialist-schedules
```

### Feature 5 — Appointments

Tablas:

```text
appointments
appointment_history
```

Endpoints principales:

```text
GET   /api/appointments
POST  /api/appointments
PUT   /api/appointments/{id}
PATCH /api/appointments/{id}/status
GET   /api/appointments/my
POST  /api/appointments/my
GET   /api/appointment-history
```

### Feature 6 — Clinical History

Tablas:

```text
medical_histories
clinical_evolutions
```

Endpoints principales:

```text
GET  /api/medical-histories
POST /api/medical-histories
GET  /api/clinical-evolutions
POST /api/clinical-evolutions
```

## Validacion

Ejecutar pruebas:

```bash
./gradlew test --no-daemon
```