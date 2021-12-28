package si.fri.rsoteam.services.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import okhttp3.*;
import si.fri.rsoteam.dtos.NotificationLogDto;
import si.fri.rsoteam.dtos.SMSObject;
import si.fri.rsoteam.services.config.RestConfig;;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.net.URI;
import java.time.Instant;
import java.util.logging.Logger;

@RequestScoped
public class SendSMSBean {
    private final Logger log = Logger.getLogger(SendSMSBean.class.getName());

    @Inject
    private NotificationLogBean logBean;

    @Inject
    private RestConfig config;

    @DiscoverService("basketball-users")
    private URI userServiceUrl;

    public NotificationLogDto sendSMS(SMSObject sms) throws Exception {

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
                .setSentAt(Instant.now())
                .setTimestamp(Instant.now());

        logBean.create(nld);

        log.info(response.message());
        if (response.body() != null) {
            log.info(response.body().string());
        }

        return nld;
    }
}
