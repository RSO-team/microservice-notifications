package si.fri.rsoteam.api.v1.health;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import si.fri.rsoteam.api.v1.filters.AuthFilter;

import javax.enterprise.context.ApplicationScoped;
import java.net.HttpURLConnection;
import java.net.URL;

@Liveness
@ApplicationScoped
public class d7HealthCheck implements HealthCheck {
    private final Logger LOG = LogManager.getLogger(d7HealthCheck.class.getName());

    private static final String url = "https://dashboard.d7networks.com/";

    @Override
    public HealthCheckResponse call() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");

            if (connection.getResponseCode() == 200) {
                return HealthCheckResponse.up(d7HealthCheck.class.getSimpleName());
            }
        } catch (Exception exception) {
            LOG.error(exception.getMessage());
        }
        return HealthCheckResponse.down(d7HealthCheck.class.getSimpleName());
    }
}