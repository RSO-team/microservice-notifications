package si.fri.rsoteam.api.v1.watches;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import java.util.logging.Logger;

@ApplicationScoped
public class ConfigWatch {
    private static final Logger log = Logger.getLogger(ConfigWatch.class.getName());

    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {

        String maintenanceModeKey = "rest-config.maintenance-mode";
        String apiKey = "rest-config.api-token";

        ConfigurationUtil.getInstance().subscribe(maintenanceModeKey, (String key, String value) -> {
            if (maintenanceModeKey.equals(key)) {
                if ("true".equals(value.toLowerCase())) {
                    log.info("Maintenance mode enabled.");
                } else {
                    log.info("Maintenance mode disabled.");
                }
            }
        });

        ConfigurationUtil.getInstance().subscribe(apiKey, (String key, String value) -> {
            if (apiKey.equals(key)) {
                log.info("API token has changed");
            }
        });
    }

}
