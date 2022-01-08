package si.fri.rsoteam.api.v1.resources;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;
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
import java.util.List;

@ApplicationScoped
@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationsResource {

    private Logger log = LogManager.getLogger(NotificationsResource.class.getName());

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
    @Log(LogParams.METRICS)
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
    @Log(LogParams.METRICS)
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
    @Log(LogParams.METRICS)
    public Response createObject(SMSObject dto) {
        try {
            return Response.ok(sms.sendSMStoNumber(dto)).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @POST
    @Path("{userId}")
    @Operation(summary = "Creates new notification, logs it and returns it", description = "Sends notification.")
    @APIResponses({
            @APIResponse(
                    description = "Notification log",
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = NotificationLogDto.class))
            ),
            @APIResponse(
                    description = "Error while sending sms",
                    responseCode = "500"
            ),
    })
    @Log(LogParams.METRICS)
    public Response sendSMStoUser(@PathParam("userId") Integer id, SMSObject dto) {
        try {
            return Response.ok(sms.sendSMStoUser(id, dto)).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/async/{userId}")
    @Operation(summary = "Creates new notification, logs it and returns it async", description = "Sends notification async.")
    @APIResponses({
            @APIResponse(
                    description = "nothing",
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = NotificationLogDto.class))
            ),
            @APIResponse(
                    description = "Error while sending sms",
                    responseCode = "500"
            ),
    })
    @Log(LogParams.METRICS)
    public Response sendSMStoUserAsync(@PathParam("userId") Integer id, SMSObject dto) {
        try {
            sms.sendSMStoUserAsync(id, dto);
            return Response.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/list")
    @Operation(summary = "Creates new notification, logs it and returns it async", description = "Sends notification async.")
    @APIResponses({
            @APIResponse(
                    description = "nothing",
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = NotificationLogDto.class))
            ),
            @APIResponse(
                    description = "Error while sending sms",
                    responseCode = "500"
            ),
    })
    @Log(LogParams.METRICS)
    public Response sendSMStoUsersAsync(List<SMSObject> dtos) {
        try {
            for (SMSObject dto : dtos) {
                sms.sendSMStoUser(dto.userId, dto);
            }
            return Response.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
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
    @Log(LogParams.METRICS)
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
    @Log(LogParams.METRICS)
    public Response deleteObject(@PathParam("objectId") Integer id) {
        bean.delete(id);
        return Response.noContent().build();
    }
}