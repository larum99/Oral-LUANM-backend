#!/usr/bin/env bash
set -euo pipefail
cd /home/ing_jonathan/projects/OdontoLuanm/Oral-LUANM-backend

: "${JWT_SECRET:?Define JWT_SECRET antes de ejecutar este script}"
: "${APP_ADMIN_PASSWORD:?Define APP_ADMIN_PASSWORD antes de ejecutar este script}"

export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"
export SERVER_PORT="${SERVER_PORT:-18080}"
export DB_PORT="${DB_PORT:-3307}"
export APP_ADMIN_EMAIL="${APP_ADMIN_EMAIL:-admin@admin.com}"

exec ./gradlew bootRun --no-daemon --console=plain