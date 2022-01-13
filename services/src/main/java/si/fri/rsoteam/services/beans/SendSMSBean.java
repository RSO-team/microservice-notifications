package si.fri.rsoteam.services.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import okhttp3.*;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import si.fri.rsoteam.dtos.NotificationLogDto;
import si.fri.rsoteam.dtos.SMSD7;
import si.fri.rsoteam.dtos.SMSObject;
import si.fri.rsoteam.dtos.UserDto;
import si.fri.rsoteam.services.config.RestConfig;
import si.fri.rsoteam.services.services.clients.UsersApi;;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

@RequestScoped
public class SendSMSBean {
    private final Logger log = LogManager.getLogger(SendSMSBean.class.getName());

    @Inject
    private NotificationLogBean logBean;

    @Inject
    private RestConfig config;

    @Inject
    private RestConfig restConfig;

    @Inject
    @DiscoverService(value = "basketball-users")
    private Optional<WebTarget> userServiceUrl;

    private Client httpClient;

    private UsersApi usersApi;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();

        userServiceUrl.ifPresent(webTarget -> usersApi = RestClientBuilder
                .newBuilder()
                .baseUri(webTarget.getUri())
                .build(UsersApi.class));
    }

    public NotificationLogDto sendSMStoNumber(SMSObject sms) throws Exception {
        return this.sendSMS(sms);
    }

    public NotificationLogDto sendSMStoUser(Integer userId, SMSObject sms) throws Exception {
        UserDto user = this.getUser(userId);
        if (user == null) {
            return null;
        }
        if (user.id != null) {
            sms.to = user.gsm;
            return this.sendSMS(sms);
        }
        return null;
    }

    public void sendSMStoUserAsync(Integer userId, SMSObject sms) {
        CompletionStage<UserDto> stringCompletionStage = this.usersApi.getUser(userId);
        stringCompletionStage.whenComplete((user, throwable) -> {
            if (user.id != null) {
                sms.to = user.gsm;
                try {
                    this.sendSMS(sms);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        stringCompletionStage.exceptionally(throwable -> {
            log.error(throwable.getMessage());
            return null;
        });
    }

    private NotificationLogDto sendSMS(SMSObject sms) throws Exception {

        SMSD7 smsd7 = new SMSD7();
        smsd7.content = sms.content;
        smsd7.to = sms.to;
        smsd7.from = sms.from;

        ObjectWriter ow = new ObjectMapper().writer();
        byte[] object = ow.writeValueAsBytes(smsd7);

        String token = config.getD7Token();

        if (token == null) {
            throw new Exception("Missing token");
        }

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(object, mediaType);
        Request request = new Request.Builder()
                .url("https://rest-api.d7networks.com/secure/send")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", String.format("Basic %s", token))
                .build();
        Response response = client.newCall(request).execute();

        NotificationLogDto nld = new NotificationLogDto()
                .setContent(sms.content)
                .setSender(sms.from)
                .setReceiver(String.valueOf(sms.to))
                .setSentAt(response.code() == 200 ? Instant.now() : null)
                .setTimestamp(Instant.now());

        logBean.create(nld);

        log.info(response.message());
        if (response.body() != null) {
            log.info(response.body().string());
        }

        response.close();

        return nld;
    }

    private UserDto getUser(Integer id) {
        if (userServiceUrl.isPresent()) {
            String host = String.format("%s/v1/users/%d",
                    userServiceUrl.get().getUri(),
                    id);
            UserDto response = httpClient
                    .target(host)
                    .request()
                    .header("apiToken", restConfig.getApiToken())
                    .get(new GenericType<>() {
                    });
            log.info(response.toString());
            return response;
        }
        return null;
    }
}
