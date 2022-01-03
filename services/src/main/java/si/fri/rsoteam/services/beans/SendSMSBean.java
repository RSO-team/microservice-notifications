package si.fri.rsoteam.services.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import okhttp3.*;
import si.fri.rsoteam.dtos.NotificationLogDto;
import si.fri.rsoteam.dtos.SMSObject;
import si.fri.rsoteam.dtos.UserDto;
import si.fri.rsoteam.services.config.RestConfig;;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.Optional;

@RequestScoped
public class SendSMSBean {
    private final Logger log = LogManager.getLogger(SendSMSBean.class.getName());

    @Inject
    private NotificationLogBean logBean;

    @Inject
    private RestConfig config;

    @Inject
    @DiscoverService(value = "basketball-users")
    private Optional<URL> userServiceUrl;

    private Client httpClient;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }

    public NotificationLogDto sendSMStoNumber(SMSObject sms) throws Exception {
        return this.sendSMS(sms);
    }

    public NotificationLogDto sendSMStoUser(Integer userId, SMSObject sms) throws Exception {
        UserDto user = this.getUser(userId);
        if (user.id != null) {
            sms.to = user.gsm;
            return this.sendSMS(sms);
        }
        return null;
    }

    private NotificationLogDto sendSMS(SMSObject sms) throws Exception {

        ObjectWriter ow = new ObjectMapper().writer();
        byte[] object = ow.writeValueAsBytes(sms);

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

        return nld;
    }



    private UserDto getUser(Integer id) {
        if (userServiceUrl.isPresent()) {
            try {
                String host = userServiceUrl.get().getProtocol() +
                        "://" +
                        userServiceUrl.get().getHost() + ":" +
                        userServiceUrl.get().getPort() +
                        "/v1/users/" +
                        id;
                UserDto response = httpClient
                        .target(host)
                        .request().get(new GenericType<>() {
                        });
                log.info(response.toString());
                return response;
            }
            catch (WebApplicationException | ProcessingException e) {
                log.error(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }
        return null;
    }
}
