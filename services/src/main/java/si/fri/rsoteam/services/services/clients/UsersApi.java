package si.fri.rsoteam.services.services.clients;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import si.fri.rsoteam.dtos.UserDto;

import javax.enterprise.context.Dependent;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.concurrent.CompletionStage;

@Path("/v1/users")
@RegisterRestClient(configKey="users-api")
@Dependent
public interface UsersApi {

    @GET
    @Path("{id}")
    CompletionStage<UserDto> getUser(@PathParam("id") Integer id);
}
