#!/bin/bash

# Script para detener el backend de Relatos de Papel

echo "🛑 Deteniendo Relatos de Papel Backend..."

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

# Detener los servicios
echo "🎯 Deteniendo servicios..."
docker-compose down

echo "✅ Servicios detenidos exitosamente!"

echo ""
echo "💡 Para eliminar volúmenes y redes (datos persistentes):"
echo "   docker-compose down -v --remove-orphans"
echo ""
echo "🔄 Para reiniciar el sistema:"
echo "   ./scripts/docker-start.sh"