# 🚀 Guía Rápida de Inicio

## Inicio Rápido (3 pasos)

### 1️⃣ Levantar Bases de Datos

```bash
docker-compose up -d
```

### 2️⃣ Iniciar Servicios (en terminales separadas)

**Terminal 1 - Eureka:**

```bash
cd eureka-server
mvn spring-boot:run
```

Esperar a ver: `Started EurekaServerApplication`

**Terminal 2 - Catálogo:**

```bash
cd ms-books-catalogue
mvn spring-boot:run
```

Esperar a ver: `Started MsBooksCatalogueApplication`

**Terminal 3 - Pagos:**

```bash
cd ms-books-payments
mvn spring-boot:run
```

Esperar a ver: `Started MsBooksPaymentsApplication`

**Terminal 4 - Gateway:**

```bash
cd api-gateway
mvn spring-boot:run
```

Esperar a ver: `Started ApiGatewayApplication`

## 📝 Ejemplos de Uso

### Crear una Categoría

```bash
curl -X POST http://18.117.141.17:8080/catalogue/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Ficción", "description": "Libros de ficción"}'
```

### Crear un Libro

```bash
curl -X POST http://18.117.141.17:8080/catalogue/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "El Quijote",
    "author": "Miguel de Cervantes",
    "isbn": "978-84-376-0494-7",
    "rating": 5,
    "visible": true,
    "stock": 10
  }'
```

### Buscar Libros

```bash
# Todos los libros
curl http://18.117.141.17:8080/catalogue/books

# Por autor
curl "http://18.117.141.17:8080/catalogue/books?author=Cervantes"

# Por rating
curl "http://18.117.141.17:8080/catalogue/books?rating=5"
```

---

## 🔗 URLs Importantes

| Servicio               | URL                   | Descripción                     |
| ---------------------- | --------------------- | ------------------------------- |
| **Eureka**             | http://18.117.141.17:8761 | Dashboard de servicios          |
| **API Gateway**        | http://18.117.141.17:8080 | Punto de entrada principal      |
| **Catálogo (directo)** | http://18.117.141.17:8081 | Acceso directo al microservicio |
| **Pagos (directo)**    | http://18.117.141.17:8082 | Acceso directo al microservicio |

---

## 🗄️ Conexión a Bases de Datos

### MySQL (Catálogo)

```bash
# Usando Docker
docker exec -it mysql_catalogue mysql -u relatos -p
# Password: relatos

USE relatos_catalogue;
SHOW TABLES;
SELECT * FROM book_entity;
SELECT * FROM category_entity;
```

### PostgreSQL (Pagos)

```bash
# Usando Docker
docker exec -it postgres_payments psql -U relatos -d relatos_payments
# Password: relatos

\dt
SELECT * FROM <tabla>;
```

---

## ⚠️ Troubleshooting

### Servicio no se registra en Eureka

1. Verificar que Eureka esté corriendo: http://18.117.141.17:8761
2. Esperar 30 segundos para el registro
3. Revisar logs del servicio

### Error de conexión a BD

```bash
# Verificar contenedores
docker-compose ps

# Ver logs
docker-compose logs mysql_catalogue
docker-compose logs postgres_payments

# Reiniciar contenedores
docker-compose restart
```

### Puerto ocupado

```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

---

## 🛑 Detener Todo

```bash
# Ctrl+C en cada terminal de Spring Boot

# Detener bases de datos
docker-compose down
```

---

## 📚 Documentación Completa

Ver [README.md](README.md) para información detallada sobre arquitectura, configuración y desarrollo.
