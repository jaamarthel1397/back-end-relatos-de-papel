#!/bin/bash

# Script para iniciar el backend de Relatos de Papel con Docker Compose

set -e

echo "🚀 Iniciando Relatos de Papel Backend..."

# Verificar que Docker esté instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Error: Docker no está instalado"
    exit 1
fi

# Verificar que docker-compose esté instalado
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Error: Docker Compose no está instalado"
    exit 1
fi

# Verificar si las imágenes existen, si no, construirlas
if ! docker-compose images &> /dev/null; then
    echo "📦 Construyendo imágenes Docker..."
    ./scripts/docker-build.sh
fi

# Iniciar los servicios
echo "🎯 Iniciando servicios..."
docker-compose up -d

# Esperar a que los servicios estén listos
echo "⏳ Esperando a que los servicios se inicien..."
sleep 30

# Verificar el estado de los servicios
echo "🔍 Verificando estado de los servicios..."
docker-compose ps

echo ""
echo "✅ Servicios iniciados exitosamente!"
echo ""
echo "🌐 URLs de los servicios:"
echo "   - Eureka Server: http://localhost:8761"
echo "   - API Gateway: http://localhost:8080"
echo "   - Books Catalogue: http://localhost:8081"
echo "   - Books Payments: http://localhost:8082"
echo "   - Swagger UI: http://localhost:8083"
echo ""
echo "📝 Para ver los logs en tiempo real:"
echo "   docker-compose logs -f"
echo ""
echo "🛑 Para detener el sistema:"
echo "   docker-compose down"