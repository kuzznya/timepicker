package space.kuzznya.timepicker.event

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.future.await
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.eclipse.microprofile.reactive.messaging.Incoming
import space.kuzznya.timepicker.StatisticsPublisher
import space.kuzznya.timepicker.UserVotes
import space.kuzznya.timepicker.VoteDao
import space.kuzznya.timepicker.VoteProcessor
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EventProcessor(
    private val eventDao: EventDao,
    private val voteDao: VoteDao,
    private val voteProcessor: VoteProcessor,
    private val statsPublisher: StatisticsPublisher,
    @Channel("event-user-votes")
    private val userVoteEmitter: Emitter<JsonObject>
) {

    @Incoming("events")
    suspend fun process(data: JsonObject) {
        val eventUpdate = data.mapTo(EventUpdate::class.java)
        val event = Event(eventUpdate.id, eventUpdate.minDate, eventUpdate.maxDate)
        eventDao.save(event).awaitSuspending()
        val deletedVotesBefore = voteDao.findAllBefore(event.id, event.minDate)
            .onItem().call { vote -> voteDao.delete(vote) }
            .collect().asList()
            .awaitSuspending()
        val deletedVotesAfter = voteDao.findAllAfter(event.id, event.maxDate)
            .onItem().call { vote -> voteDao.delete(vote) }
            .collect().asList()
            .awaitSuspending()

        (deletedVotesBefore + deletedVotesAfter).map { it.username }
            .distinct()
            .forEach { username ->
                val userVotes = voteProcessor.aggregateUserVotes(username, event.id)
                userVoteEmitter.send(JsonObject.mapFrom(userVotes)).await()
            }
        statsPublisher.publishStats(event.id)
    }
}
