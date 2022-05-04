package space.kuzznya.timepicker

import io.quarkus.security.Authenticated
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.UUID
import javax.ws.rs.GET
import javax.ws.rs.NotFoundException
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Path("/api/votes")
@Authenticated
class VoteResource(
    private val accessToken: JsonWebToken,
    private val voteDao: VoteDao,
    private val voteProcessor: VoteProcessor
) {

    @GET
    @Path("/{eventId}")
    fun getUserVotes(@PathParam("eventId") eventId: UUID) =
        voteDao.findAllForUserAndEvent(accessToken.name, eventId)

    @GET
    @Path("/{eventId}/all")
    suspend fun getAllVotes(@PathParam("eventId") eventId: UUID): List<Vote> =
        voteDao.findAllForEvent(eventId).collect().asList()
            .onItem().ifNull().failWith { NotFoundException("Event $eventId not found") }
            .awaitSuspending()

    @GET
    @Path("/{eventId}/stats")
    suspend fun getStatistics(@PathParam("eventId") eventId: UUID) =
        voteProcessor.calculateStats(eventId)
}
