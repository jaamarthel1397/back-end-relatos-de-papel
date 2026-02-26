package com.relatospapel.ms_books_catalogue.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {
  @Bean
  public OpenAPI customOpenAPI() {
    Server localServer = new Server();
    localServer.setUrl("http://3.143.207.187:8081");
    localServer.setDescription("Servidor local - MS Books Catalogue");

    Server gatewayServer = new Server();
    gatewayServer.setUrl("http://3.143.207.187:8080");
    gatewayServer.setDescription("API Gateway - Acceso recomendado");

    Contact contact = new Contact();
    contact.setName("Relatos de Papel - Catálogo");
    contact.setEmail("contacto@relatospapel.com");

    Info info = new Info()
        .title("API de Catálogo de Libros")
        .version("1.0.0")
        .description("Microservicio para la gestión del catálogo de libros y categorías. " +
            "Incluye operaciones CRUD completas y búsqueda avanzada.")
        .contact(contact);

    return new OpenAPI()
        .info(info)
        .servers(List.of(gatewayServer, localServer));
  }
}
