package space.kuzznya.timepicker

import io.quarkus.smallrye.reactivemessaging.ackSuspending
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.core.json.JsonObject
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.eclipse.microprofile.reactive.messaging.Message
import org.eclipse.microprofile.reactive.messaging.Outgoing
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.kuzznya.timepicker.event.Event
import space.kuzznya.timepicker.event.EventDao
import java.time.LocalDate
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class VoteProcessor(
    private val voteDao: VoteDao,
    private val eventDao: EventDao,
    private val statsPublisher: StatisticsPublisher
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(VoteProcessor::class.java)
    }

    @Incoming("votes")
    @Outgoing("user-votes")
    suspend fun process(voteData: Message<JsonObject>): Message<JsonObject> {
        val vote = voteData.payload.mapTo(Vote::class.java)
        log.info("Vote received: $vote")
        val event = eventDao.findById(vote.eventId)
            .onItem().ifNull().continueWith { Event(vote.eventId, LocalDate.MIN, LocalDate.MAX) }
            .awaitSuspending()
        if (vote.date >= event.minDate && vote.date <= event.maxDate) {
            if (voteData.payload.getValue("state") == VoteState.VOTED.name)
                voteDao.save(vote).awaitSuspending()
            else
                voteDao.delete(vote).awaitSuspending()
        } else {
            log.warn("Vote ignored: date ${vote.date} is out of bounds ${event.minDate} - ${event.maxDate}")
        }
        voteData.ackSuspending()
        statsPublisher.publishStats(vote.eventId)
        val userVotes = aggregateUserVotes(vote.username, vote.eventId)
        return Message.of(JsonObject.mapFrom(userVotes))
    }

    suspend fun aggregateUserVotes(username: String, eventId: UUID): UserVotes =
        voteDao.findAllForUserAndEvent(username, eventId)
            .collect()
            .asList()
            .awaitSuspending()
            .map { it.date }
            .let { votes -> UserVotes(eventId, username, votes) }
}
