package edu.ifrs;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import jakarta.inject.Inject;

@Readiness
public class CatalogHealthCheck implements HealthCheck {

    @Inject
    @RestClient
    IBookCatalog bookCatalog;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder response = HealthCheckResponse.named("Book Catalog connection check");

        try {
            //tenta chamar o serviço externo
            bookCatalog.listBooks();
            
            //se não der erro, diz que está tudo certo
            response.up();
            
        } catch (Exception e) {
            //se der erro cai direto aqui
            response.down();
        }

        return response.build();
    }
}