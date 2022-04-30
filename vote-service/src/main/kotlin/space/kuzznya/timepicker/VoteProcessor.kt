package space.kuzznya.timepicker

import io.quarkus.smallrye.reactivemessaging.ackSuspending
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.future.await
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Message
import org.eclipse.microprofile.reactive.messaging.Outgoing
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class VoteProcessor(
    private val voteDao: VoteDao,
    @Channel("statistics")
    private val statsEmitter: Emitter<JsonObject>
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(VoteProcessor::class.java)
    }

    @Incoming("votes")
    @Outgoing("user-votes")
    suspend fun process(voteData: Message<JsonObject>): Message<JsonObject> {
        val vote = voteData.payload.mapTo(Vote::class.java)
        log.info("Vote received: $vote")
        if (voteData.payload.getValue("state") == VoteState.VOTED.name) voteDao.save(vote).awaitSuspending()
        else voteDao.delete(vote).awaitSuspending()
        voteData.ackSuspending()
        publishStats(vote.eventId)
        val userVotes = aggregateUserVotes(vote.username, vote.eventId)
        return Message.of(JsonObject.mapFrom(userVotes))
    }

    suspend fun publishStats(event: UUID) {
        val stats = calculateStats(event)
        log.info("Statistics calculated: $stats")
        statsEmitter.send(JsonObject.mapFrom(stats)).await()
    }

    suspend fun calculateStats(event: UUID): Statistics {
        val votes = voteDao.findAllForEvent(event).collect().asList().awaitSuspending()
        return Statistics(event, votes.groupingBy { it.date }.eachCount())
    }

    private suspend fun aggregateUserVotes(username: String, eventId: UUID): UserVotes =
        voteDao.findAllForUserAndEvent(username, eventId)
            .collect()
            .asList()
            .awaitSuspending()
            .map { it.date }
            .let { votes -> UserVotes(eventId, username, votes) }
}
