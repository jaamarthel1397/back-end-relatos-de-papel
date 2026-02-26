# Multi-stage Dockerfile para el backend de Relatos de Papel

# Fase 1: Construcción
FROM maven:3.9-eclipse-temurin-21 AS builder

# Crear directorio de trabajo
WORKDIR /app

# Copiar pom.xml y descargar dependencias (para aprovechar el cache de Docker)
COPY pom.xml .
COPY */pom.xml */pom.xml
COPY */*/pom.xml */*/pom.xml

# Descargar dependencias
RUN mvn dependency:resolve -Dmaven.test.skip=true
RUN mvn dependency:resolve-plugins -Dmaven.test.skip=true

# Copiar el código fuente
COPY . .

# Construir los proyectos
RUN mvn clean package -Dmaven.test.skip=true

# Fase 2: Producción
FROM eclipse-temurin:21-jre

# Crear usuario no root para mayor seguridad
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Crear directorio de la aplicación
WORKDIR /app

# Copiar archivos jar construidos
COPY --from=builder /app/api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar ./api-gateway.jar
COPY --from=builder /app/eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar ./eureka-server.jar
COPY --from=builder /app/ms-books-catalogue/target/ms-books-catalogue-0.0.1-SNAPSHOT.jar ./ms-books-catalogue.jar
COPY --from=builder /app/ms-books-payments/target/ms-books-payments-0.0.1-SNAPSHOT.jar ./ms-books-payments.jar

# Cambiar propietario de los archivos
RUN chown -R appuser:appuser /app

# Cambiar al usuario no root
USER appuser

# Exponer puertos
EXPOSE 8761 8080 8081 8082

# Comando por defecto
CMD ["sh", "-c", "echo 'Starting Relatos de Papel Backend...' && echo 'Available services:' && echo ' - Eureka Server: http://localhost:8761' && echo ' - API Gateway: http://localhost:8080' && echo ' - Books Catalogue: http://localhost:8081' && echo ' - Books Payments: http://localhost:8082' && tail -f /dev/null"]