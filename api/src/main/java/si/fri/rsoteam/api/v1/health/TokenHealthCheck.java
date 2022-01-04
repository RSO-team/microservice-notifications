package si.fri.rsoteam.api.v1.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import si.fri.rsoteam.services.config.RestConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Liveness
@ApplicationScoped
public class TokenHealthCheck implements HealthCheck {

    @Inject
    private RestConfig restProperties;

    @Override
    public HealthCheckResponse call() {
        if (restProperties.getD7Token() == null) {
            return HealthCheckResponse.down(TokenHealthCheck.class.getSimpleName());
        } else {
            return HealthCheckResponse.up(TokenHealthCheck.class.getSimpleName());
        }
    }
}