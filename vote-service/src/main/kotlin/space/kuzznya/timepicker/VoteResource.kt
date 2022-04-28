package space.kuzznya.timepicker

import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.UUID
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam

@Path("/votes")
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
    @Path("/{eventId}/stats")
    suspend fun getStatistics(@PathParam("eventId") eventId: UUID) =
        voteProcessor.calculateStats(eventId)
}
