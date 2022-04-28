package space.kuzznya.timepicker

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.smallrye.reactive.messaging.annotations.Blocking
import io.vertx.core.json.JsonObject
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Outgoing
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class VoteProcessor(
    private val voteDao: VoteDao
) {

    @Incoming("votes")
    @Outgoing("statistics")
//    @Blocking("kafka-worker", ordered = true)
    suspend fun process(voteData: JsonObject): JsonObject {
        val vote = voteData.mapTo(Vote::class.java)
        if (vote.state == VoteState.VOTED) voteDao.save(vote).awaitSuspending()
        else voteDao.delete(vote).awaitSuspending()
        return JsonObject.mapFrom(calculateStats(vote.eventId))
    }

    suspend fun calculateStats(event: UUID): Statistics {
        val votes = voteDao.findAllForEvent(event).collect().asList().awaitSuspending()
        return Statistics(event, votes.groupingBy { it.date }.eachCount())
    }
}
