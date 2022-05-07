package space.kuzznya.timepicker

import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.core.json.JsonObject
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EventProcessor(
    private val eventDao: EventDao,
    private val voteDao: VoteDao
) {

    @Incoming("events")
    suspend fun process(data: JsonObject) {
        val eventUpdate = data.mapTo(EventUpdate::class.java)
        val event = Event(eventUpdate.id, eventUpdate.minDate, eventUpdate.maxDate)
        eventDao.save(event).awaitSuspending()
        voteDao.deleteVotesBefore(event.id, event.minDate).awaitSuspending()
        voteDao.deleteVotesAfter(event.id, event.maxDate).awaitSuspending()
    }
}
