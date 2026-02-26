package com.relatospapel.ms_books_payments.config;

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
        localServer.setUrl("http://3.143.207.187:8082");
        localServer.setDescription("Servidor local - MS Books Payments");

        Server gatewayServer = new Server();
        gatewayServer.setUrl("http://3.143.207.187:8080");
        gatewayServer.setDescription("API Gateway - Acceso recomendado");

        Contact contact = new Contact();
        contact.setName("Relatos de Papel - Pagos");
        contact.setEmail("contacto@relatospapel.com");

        Info info = new Info()
            .title("API de Pagos y Compras")
            .version("1.0.0")
            .description("Microservicio para la gestión de pagos, órdenes de compra y clientes.")
            .contact(contact);

        return new OpenAPI()
            .info(info)
            .servers(List.of(gatewayServer, localServer));
    }
}
//