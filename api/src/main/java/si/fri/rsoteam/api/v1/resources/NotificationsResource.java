package si.fri.rsoteam.api.v1.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rsoteam.dtos.NotificationLogDto;
import si.fri.rsoteam.dtos.SMSObject;
import si.fri.rsoteam.services.beans.NotificationLogBean;
import si.fri.rsoteam.services.beans.SendSMSBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationsResource {

    private Logger log = Logger.getLogger(NotificationsResource.class.getName());

    @Inject
    private NotificationLogBean bean;

    @Inject
    private SendSMSBean sms;

    @Context
    protected UriInfo uriInfo;

    @GET
    @Operation(summary = "Get list of notification logs", description = "Returns list of notification logs.")
    @APIResponses({
            @APIResponse(
                    description = "notification logs list",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = NotificationLogDto.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )
    })
    public Response getObjects() {
        List<NotificationLogDto> list = bean.getList();
        return Response.ok(list).build();
    }

    @GET
    @Operation(summary = "Get notification log by given id", description = "Returns notification log")
    @APIResponses({
            @APIResponse(
                    description = "Activity point details",
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = NotificationLogDto.class))
            )
    })
    @Path("/{id}")
    public Response getObjectById(@PathParam("id") Integer id) {
        NotificationLogDto object = bean.get(id);
        if (object == null) {
            throw new NotFoundException("Object not found");
        }
        return Response.ok(object).build();
    }

    @POST
    @Operation(summary = "Creates new notification, logs it and returns it", description = "Sends notification.")
    @APIResponses({
            @APIResponse(
                    description = "User details",
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = NotificationLogDto.class))
            ),
            @APIResponse(
                    description = "Error while sending sms",
                    responseCode = "500"
            ),
    })
    public Response createObject(SMSObject dto) {
        try {
            return Response.ok(sms.sendSMS(dto)).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @PUT
    @Operation(summary = "Updates notification log and returns it", description = "Returns notification log.")
    @APIResponses({
            @APIResponse(
                    description = "Activity point details",
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = NotificationLogDto.class))
            )
    })
    @Path("{objectId}")
    public Response updateObjectById(@PathParam("objectId") Integer id,
                                     NotificationLogDto object) {
        return Response.status(201).entity(bean.update(object, id)).build();
    }

    @DELETE
    @Operation(summary = "Deletes specified object", description = "Returns no content.")
    @APIResponses({
            @APIResponse(
                    description = "No content",
                    responseCode = "204"
            )
    })
    @Path("{objectId}")
    public Response deleteObject(@PathParam("objectId") Integer id) {
        bean.delete(id);
        return Response.noContent().build();
    }
}