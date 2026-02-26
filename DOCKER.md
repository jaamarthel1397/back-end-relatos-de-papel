# Despliegue Docker - Relatos de Papel Backend

Guía completa para desplegar el backend de Relatos de Papel utilizando Docker y Docker Compose.

## 📋 Requisitos Previos

- Docker 20.10 o superior
- Docker Compose 2.0 o superior
- 4GB de RAM disponible
- Puerto 5432, 8080-8083 disponibles

## 🚀 Despliegue Rápido

### 1. Clonar el proyecto

```bash
git clone <tu-repositorio>
cd back-end-relatos-de-papel
```

### 2. Construir las imágenes

```bash
./scripts/docker-build.sh
```

### 3. Iniciar el sistema

```bash
./scripts/docker-start.sh
```

### 4. Verificar el despliegue

```bash
docker-compose ps
```

## 🏗️ Arquitectura del Despliegue

El despliegue incluye los siguientes servicios:

### Base de Datos

- **PostgreSQL**: Base de datos principal (puerto 5433)
  - Base de datos: `relatos_database`
  - Usuario: `relatos`
  - Contraseña: `relatos`

### Servicios de Backend

- **Eureka Server**: Descubrimiento de servicios (puerto 8761)
- **API Gateway**: Gateway de API (puerto 8080)
- **Books Catalogue**: Microservicio de catálogo (puerto 8081)
- **Books Payments**: Microservicio de pagos (puerto 8082)

### Herramientas

- **Swagger UI**: Documentación de API (puerto 8083)

## 📁 Estructura de Archivos

```
back-end-relatos-de-papel/
├── Dockerfile              # Dockerfile multi-stage
├── docker-compose.yml      # Orquestación de servicios
├── scripts/
│   ├── docker-build.sh     # Script de construcción
│   ├── docker-start.sh     # Script de inicio
│   └── docker-stop.sh      # Script de detención
└── README.md
```

## 🔧 Configuración Personalizada

### Variables de Entorno

Puedes crear un archivo `.env` en la raíz para personalizar la configuración:

```bash
# Base de datos
POSTGRES_DB=relatos_database
POSTGRES_USER=relatos
POSTGRES_PASSWORD=tu_contraseña_segura

# OpenSearch (opcional)
OPENSEARCH_URL=https://tu-cluster:tu-contraseña@cluster.bonsaisearch.net

# Puertos personalizados
POSTGRES_PORT=5433
GATEWAY_PORT=8080
CATALOGUE_PORT=8081
PAYMENTS_PORT=8082
```

### Perfiles de Spring Boot

Los servicios utilizan el perfil `docker` cuando se ejecutan en contenedores. Puedes crear archivos de configuración específicos:

- `application-docker.properties` para configuraciones en Docker
- `application-production.properties` para entornos de producción

## 🛠️ Comandos Útiles

### Gestión de Contenedores

```bash
# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f api-gateway

# Acceder a un contenedor
docker-compose exec api-gateway bash

# Ver estadísticas de recursos
docker-compose top
```

### Gestión de Imágenes

```bash
# Ver imágenes creadas
docker images | grep relatos

# Eliminar imágenes no utilizadas
docker image prune

# Reconstruir imágenes
docker-compose build --no-cache
```

### Gestión de Datos

```bash
# Ver volúmenes
docker volume ls

# Acceder a datos de PostgreSQL
docker-compose exec postgres psql -U relatos -d relatos_database

# Hacer backup de la base de datos
docker-compose exec postgres pg_dump -U relatos relatos_database > backup.sql
```

## 🔍 Monitoreo y Salud

### Health Checks

Todos los servicios incluyen health checks que puedes verificar:

```bash
# Ver estado de salud
curl http://localhost:8761/actuator/health
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

### Métricas

Los servicios exponen métricas en `/actuator/metrics`:

```bash
# Ver métricas generales
curl http://localhost:8080/actuator/metrics

# Ver métricas específicas
curl "http://localhost:8080/actuator/metrics/jvm.memory.used"
```

## 🚨 Solución de Problemas

### Problemas Comunes

1. **Puertos ocupados**

   ```bash
   # Ver qué procesos usan los puertos
   lsof -i :8080
   # O en Windows
   netstat -ano | findstr :8080
   ```

2. **Espacio en disco insuficiente**

   ```bash
   # Limpiar imágenes y contenedores no utilizados
   docker system prune -a
   ```

3. **Problemas de red**

   ```bash
   # Reconstruir redes
   docker-compose down -v
   docker-compose up -d
   ```

4. **Errores de dependencias**
   ```bash
   # Forzar reconstrucción
   docker-compose build --no-cache
   ```

### Logs de Errores

```bash
# Ver logs de errores específicos
docker-compose logs --tail=100 api-gateway | grep ERROR

# Ver logs en tiempo real con filtro
docker-compose logs -f | grep -E "(ERROR|WARN)"
```

## 🔄 Despliegue en Producción

### Consideraciones de Producción

1. **Seguridad**
   - Cambiar contraseñas por defecto
   - Usar certificados SSL/TLS
   - Configurar firewalls

2. **Persistencia**
   - Configurar backups automáticos
   - Usar almacenamiento externo para volúmenes

3. **Escalabilidad**
   - Configurar balanceo de carga
   - Escalar servicios según necesidad

4. **Monitoreo**
   - Configurar alertas
   - Integrar con herramientas de monitoreo

### Script de Producción

```bash
# Para entornos de producción, usa:
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## 📞 Soporte

Para soporte o reporte de issues:

1. Verifica los logs de los contenedores
2. Revisa los health checks
3. Consulta esta documentación
4. Reporta issues en el repositorio

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo LICENSE para más detalles.
