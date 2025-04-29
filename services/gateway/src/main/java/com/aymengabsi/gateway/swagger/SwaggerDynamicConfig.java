package com.aymengabsi.gateway.swagger;

import jakarta.annotation.PostConstruct;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;


@Configuration
public class SwaggerDynamicConfig {

    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private SwaggerUiConfigParameters swaggerUiConfigParameters;

    @PostConstruct
    public void init() {
        System.out.println("🔄 Initializing Swagger Dynamic Config...");

        routeLocator.getRoutes()
                .filter(route -> route.getId() != null)
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .map(Route::getId)
                .filter(this::isSwaggerRouteId)
                .map(this::extractServiceName)
                .filter(serviceName -> serviceName != null && !serviceName.isBlank())
                .distinct()
                .doOnNext(serviceName -> {
                    String path = "/v3/api-docs/" + serviceName;
                    System.out.println("✅ Registering Swagger Group: " + serviceName + " ➝ " + path);
                    swaggerUiConfigParameters.addGroup(serviceName, serviceName);
                })
                .subscribe();
    }

    private boolean isSwaggerRouteId(String routeId) {
        // Only include the Swagger doc route entries like: customer-swagger, product-service-swagger
        return routeId.endsWith("-swagger");
    }

    private String extractServiceName(String routeId) {
        // Extract the service name cleanly
        return routeId.replace("-swagger", "");
    }
}



