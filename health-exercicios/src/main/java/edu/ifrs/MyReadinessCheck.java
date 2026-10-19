package edu.ifrs;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

@Readiness
public class MyReadinessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder response = HealthCheckResponse.named("Database connection health check");
        
        //response.down(); exercicio 2
        response.up().withData("version", "1.0.0"); // exercicio 3
        
        return response.build();
        
    }

}