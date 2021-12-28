package si.fri.rsoteam.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ConfigBundle("rest-config")
@ApplicationScoped
public class RestConfig {
    @ConfigValue(watch = true)
    Boolean maintenanceMode;

    @ConfigValue(watch = true)
    String apiToken;

    @ConfigValue(watch = true)
    String d7Username;

    @ConfigValue(watch = true)
    String d7Password;

    @ConfigValue(watch = true)
    String d7Token;

    public Boolean getMaintenanceMode() {
        return this.maintenanceMode;
    }

    public void setMaintenanceMode(final Boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getD7Username() {
        return d7Username;
    }

    public void setD7Username(String d7Username) {
        this.d7Username = d7Username;
    }

    public String getD7Password() {
        return d7Password;
    }

    public void setD7Password(String d7Password) {
        this.d7Password = d7Password;
    }

    public String getD7Token() {
        return d7Token;
    }

    public void setD7Token(String d7Token) {
        this.d7Token = d7Token;
    }
}
