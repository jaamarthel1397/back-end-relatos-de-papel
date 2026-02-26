package com.relatospapel.api_gateway.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        Server gatewayServer = new Server();
        gatewayServer.setUrl("http://18.117.141.17:8080");
        gatewayServer.setDescription("API Gateway - Relatos de Papel");

        Contact contact = new Contact();
        contact.setName("Relatos de Papel");
        contact.setEmail("contacto@relatospapel.com");

        Info info = new Info()
            .title("Relatos de Papel API")
            .version("1.0.0")
            .description("API Gateway para todos los microservicios de Relatos de Papel")
            .contact(contact);

        return new OpenAPI()
            .info(info)
            .servers(List.of(gatewayServer));
    }
}
