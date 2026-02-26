#!/bin/bash

# Script para construir las imágenes Docker del backend

set -e

echo "🚀 Construyendo imágenes Docker para Relatos de Papel Backend..."

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

# Construir las imágenes
echo "📦 Construyendo imágenes..."
docker-compose build --no-cache

echo "✅ Imágenes construidas exitosamente!"

echo ""
echo "📋 Resumen de imágenes creadas:"
docker images | grep relatos

echo ""
echo "🎯 Para iniciar el sistema completo, ejecuta:"
echo "   docker-compose up -d"
echo ""
echo "🔍 Para ver los logs:"
echo "   docker-compose logs -f"
echo ""
echo "🛑 Para detener el sistema:"
echo "   docker-compose down"