package space.kuzznya.timepicker

import io.quarkus.security.Authenticated
import io.smallrye.mutiny.Multi
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.UUID
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Path("/api/events")
@Authenticated
class EventResource(
    private val accessToken: JsonWebToken,
    private val eventService: EventService
) {

    @POST
    fun add(event: Event) = eventService.save(event, accessToken.name)

    @PUT
    @Path("/{id}/dates")
    suspend fun updateDates(@PathParam("id") id: UUID, request: DateUpdateRequest) =
        eventService.updateDates(id, accessToken.name, request)

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: UUID) = eventService.delete(id, accessToken.name)

    @GET
    fun getAll(): Multi<Event> = eventService.findAllForParticipant(accessToken.name)

    @GET
    @Path("/{id}")
    fun getOne(@PathParam("id") id: UUID) = eventService.findOne(id, accessToken.name)
}
