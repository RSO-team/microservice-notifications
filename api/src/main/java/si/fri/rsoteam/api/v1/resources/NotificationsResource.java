package si.fri.rsoteam.api.v1.resources;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationsResource {

    private Logger log = Logger.getLogger(NotificationsResource.class.getName());

//    @Inject
//    private EventsBean eventsBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getObjects() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/{objectId}")
    public Response getObjectById(@PathParam("objectId") Integer id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    public Response createObject(Object object) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();

    }

    @PUT
    @Path("{objectId}")
    public Response putObjectById(@PathParam("objectId") Integer id,
                                     Object object) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("{objectId}")
    public Response deleteObject(@PathParam("objectId") Integer id) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}