package space.kuzznya.timepicker

import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class EventService(
    private val eventDao: EventDao
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(EventService::class.java)
    }

    fun save(event: Event, author: String) = eventDao.save(
        event.copy(
            id = UUID.randomUUID(),
            participant = author,
            author = author
        ).also { log.info("Creating event $it") }
    )

    fun delete(id: UUID, participant: String) = eventDao.delete(id, participant)
        .also { log.info("Event $id, participant $participant deleted") }

    fun findOne(id: UUID, participant: String): Uni<Event> = eventDao.findById(id, participant)
        .invoke { event -> log.info("Retrieved event $event") }

    fun findAllForParticipant(participant: String): Multi<Event> = eventDao.findByParticipant(participant)
        .invoke { event -> log.info("Retrieved event $event (searching all for participant)") }
}
