# Relatos de Papel - Backend

Sistema de microservicios para la gestión de catálogo de libros y pagos.

## 📋 Arquitectura

El proyecto está compuesto por los siguientes servicios:

- **Eureka Server** (Puerto 8761): Servidor de descubrimiento de servicios
- **API Gateway** (Puerto 8080): Punto de entrada único para todas las peticiones
- **MS Books Catalogue** (Puerto 8081): Microservicio de catálogo de libros
- **MS Books Payments** (Puerto 8082): Microservicio de pagos

## 🛠️ Tecnologías

- Java 17
- Spring Boot 4.0.2
- Spring Cloud 2025.1.0
- MySQL 8.4 (Catálogo)
- PostgreSQL 16 (Pagos)
- Docker & Docker Compose

## 📦 Requisitos Previos

- Java JDK 17 o superior
- Maven 3.6+
- Docker y Docker Compose
- Git

## 🚀 Instalación y Ejecución

### 1. Clonar el Repositorio

```bash
git clone <repository-url>
cd back-end-relatos-de-papel
```

### 2. Levantar las Bases de Datos

Iniciar los contenedores de MySQL y PostgreSQL:

```bash
docker-compose up -d
```

Esto creará:

- **MySQL** en `18.117.141.17:3307`
  - Base de datos: `relatos_catalogue`
  - Usuario: `relatos`
  - Contraseña: `relatos`

- **PostgreSQL** en `18.117.141.17:5433`
  - Base de datos: `relatos_payments`
  - Usuario: `relatos`
  - Contraseña: `relatos`

### 3. Verificar que las Bases de Datos están Corriendo

```bash
# Ver logs de los contenedores
docker-compose logs -f

# Verificar estado
docker-compose ps
```

### 4. Compilar el Proyecto

Compilar todos los microservicios:

```bash
# Compilar Eureka Server
cd eureka-server
mvn clean install
cd ..

# Compilar API Gateway
cd api-gateway
mvn clean install
cd ..

# Compilar MS Books Catalogue
cd ms-books-catalogue
mvn clean install
cd ..

# Compilar MS Books Payments
cd ms-books-payments
mvn clean install
cd ..
```

### 5. Iniciar los Servicios (en orden)

**Orden de inicio importante:**

#### 1️⃣ Eureka Server (primero)

```bash
cd eureka-server
mvn spring-boot:run
```

Esperar hasta ver: `Started EurekaServerApplication`

Verificar en: http://18.117.141.17:8761

#### 2️⃣ MS Books Catalogue

```bash
cd ms-books-catalogue
mvn spring-boot:run
```

Esperar hasta ver: `Started MsBooksCatalogueApplication`

#### 3️⃣ MS Books Payments

```bash
cd ms-books-payments
mvn spring-boot:run
```

Esperar hasta ver: `Started MsBooksPaymentsApplication`

#### 4️⃣ API Gateway (último)

```bash
cd api-gateway
mvn spring-boot:run
```

Esperar hasta ver: `Started ApiGatewayApplication`

## 🌐 Endpoints Principales

Todas las peticiones deben pasar por el API Gateway (puerto 8080):

#### Catálogo de Libros

```
GET    http://18.117.141.17:8080/catalogue/books
POST   http://18.117.141.17:8080/catalogue/books
GET    http://18.117.141.17:8080/catalogue/books/{id}
PATCH  http://18.117.141.17:8080/catalogue/books/{id}
DELETE http://18.117.141.17:8080/catalogue/books/{id}
GET    http://18.117.141.17:8080/catalogue/books/{id}/availability?quantity=5

GET    http://18.117.141.17:8080/catalogue/categories
POST   http://18.117.141.17:8080/catalogue/categories
GET    http://18.117.141.17:8080/catalogue/categories/{id}
```

#### Pagos

```
http://18.117.141.17:8080/payments/**
```

## 🗄️ Conexión a las Bases de Datos

### MySQL (Catálogo)

**Usando MySQL Workbench o CLI:**

```bash
mysql -h 18.117.141.17 -P 3307 -u relatos -p
# Password: relatos

USE relatos_catalogue;
SHOW TABLES;
```

**Usando Docker:**

```bash
docker exec -it mysql_catalogue mysql -u relatos -p relatos_catalogue
# Password: relatos
```

### PostgreSQL (Pagos)

**Usando pgAdmin o psql:**

```bash
psql -h 18.117.141.17 -p 5433 -U relatos -d relatos_payments
# Password: relatos

\dt  -- Listar tablas
```

**Usando Docker:**

```bash
docker exec -it postgres_payments psql -U relatos -d relatos_payments
```

## 📊 Monitoreo

### Eureka Dashboard

Visualizar todos los servicios registrados:

```
http://18.117.141.17:8761
```

### Actuator Endpoints

- **API Gateway:** http://18.117.141.17:8080/actuator
- **MS Catalogue:** http://18.117.141.17:8081/actuator
- **MS Payments:** http://18.117.141.17:8082/actuator

## 🧪 Probar la API

### Ejemplo: Crear una Categoría

```bash
curl -X POST http://18.117.141.17:8080/catalogue/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ficción",
    "description": "Libros de ficción"
  }'
```

### Ejemplo: Crear un Libro

```bash
curl -X POST http://18.117.141.17:8080/catalogue/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Cien Años de Soledad",
    "author": "Gabriel García Márquez",
    "isbn": "978-0307474728",
    "rating": 5,
    "visible": true,
    "stock": 10,
    "publicationDate": "1967-05-30"
  }'
```

### Ejemplo: Buscar Libros

```bash
# Buscar todos los libros
curl http://18.117.141.17:8080/catalogue/books

# Buscar por autor
curl "http://18.117.141.17:8080/catalogue/books?author=García"

# Buscar por categoría
curl "http://18.117.141.17:8080/catalogue/books?categoryId=<uuid>"
```

## ⚠️ Solución de Problemas

### Las bases de datos no inician

```bash
# Verificar que los puertos 3307 y 5433 no estén ocupados
docker-compose down
docker-compose up -d

# Ver logs
docker-compose logs mysql_catalogue
docker-compose logs postgres_payments
```

### Error de conexión a Eureka

- Asegurarse de que Eureka Server esté corriendo primero
- Verificar que esté accesible en http://18.117.141.17:8761
- Esperar 30 segundos para que los servicios se registren

### Error de conexión a base de datos

- Verificar que Docker esté corriendo
- Verificar que los contenedores estén en estado "healthy"
- Revisar las credenciales en `application.properties`

### Puerto ya en uso

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

## 🛑 Detener los Servicios

### Detener aplicaciones Spring Boot

`Ctrl + C` en cada terminal

### Detener bases de datos

```bash
docker-compose down

# Para eliminar también los volúmenes (datos)
docker-compose down -v
```

## 📝 Estructura del Proyecto

```
back-end-relatos-de-papel/
├── api-gateway/           # API Gateway
├── eureka-server/         # Servidor de descubrimiento
├── ms-books-catalogue/    # Microservicio de catálogo (MySQL)
├── ms-books-payments/     # Microservicio de pagos (PostgreSQL)
├── scripts/
│   ├── mysql-init/       # Scripts de inicialización MySQL
│   └── postgres-init/    # Scripts de inicialización PostgreSQL
└── docker-compose.yml    # Configuración de bases de datos
```

## 📚 Validaciones en Español

Todos los mensajes de error y validación están en español:

- "El título es obligatorio"
- "Libro no encontrado"
- "La calificación mínima es 1"
- etc.

## 🔗 Enlaces Útiles

- **Eureka:** http://18.117.141.17:8761
- **API Gateway:** http://18.117.141.17:8080
- **API Gateway Health:** http://18.117.141.17:8080/actuator/health

---

## 👨‍💻 Desarrollo

Para desarrollo, se recomienda:

1. Usar un IDE como IntelliJ IDEA o VS Code
2. Instalar extensiones para Spring Boot
3. Configurar formateo automático de código
4. Usar perfiles de Spring para diferentes entornos

## 📄 Licencia

Este proyecto es parte de la Maestría en Desarrollo Web Full Stack.
