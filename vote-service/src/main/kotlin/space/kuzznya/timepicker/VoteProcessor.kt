package space.kuzznya.timepicker

import io.quarkus.smallrye.reactivemessaging.ackSuspending
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.core.json.JsonObject
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Message
import org.eclipse.microprofile.reactive.messaging.Outgoing
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class VoteProcessor(
    private val voteDao: VoteDao
) {

    @Incoming("votes")
    @Outgoing("statistics")
    suspend fun process(voteData: Message<JsonObject>): Message<JsonObject> {
        val vote = voteData.payload.mapTo(Vote::class.java)
        println(vote)
        if (voteData.payload.getValue("state") == VoteState.VOTED.name) voteDao.save(vote).awaitSuspending()
        else voteDao.delete(vote).awaitSuspending()
        voteData.ackSuspending()
        val stats = calculateStats(vote.eventId)
        println(stats)
        return Message.of(JsonObject.mapFrom(stats))
    }

    suspend fun calculateStats(event: UUID): Statistics {
        val votes = voteDao.findAllForEvent(event).collect().asList().awaitSuspending()
        return Statistics(event, votes.groupingBy { it.date }.eachCount())
    }
}
