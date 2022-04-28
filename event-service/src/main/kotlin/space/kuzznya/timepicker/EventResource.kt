package space.kuzznya.timepicker

import io.smallrye.mutiny.Multi
import org.eclipse.microprofile.jwt.JsonWebToken
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/events")
class EventResource(
    private val accessToken: JsonWebToken,
    private val eventService: EventService
) {

    @POST
    fun add(event: Event) = eventService.save(event, accessToken.name)

    @GET
    fun getAll(): Multi<Event> = eventService.findAllForParticipant(accessToken.name)
}
