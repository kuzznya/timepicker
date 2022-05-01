package space.kuzznya.timepicker

import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.NotFoundException

@ApplicationScoped
class EventService(
    private val eventDao: EventDao
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(EventService::class.java)
    }

    fun save(event: Event, author: String): Uni<Void> {
        val newEvent = event.copy(
            id = UUID.randomUUID(),
            participant = author,
            author = author
        )
        if (newEvent.minDate > newEvent.maxDate) throw RuntimeException("Min date cannot be more than max date")
        log.info("Creating event $newEvent")
        return eventDao.save(newEvent)
    }

    suspend fun updateDates(id: UUID, user: String, request: DateUpdateRequest): Event {
        val participantEvent: Event = eventDao.findByIdForParticipant(id, user)
            .onItem().ifNull().failWith { NotFoundException("Event $id not found") }
            .awaitSuspending()
        eventDao.findById(id)
            .map { it.copy(minDate = request.minDate, maxDate = request.maxDate) }
            .call { event -> eventDao.save(event) }
            .collect().asList().awaitSuspending()
        return participantEvent
    }

    fun delete(id: UUID, participant: String) = eventDao.delete(id, participant)
        .also { log.info("Event $id, participant $participant deleted") }

    fun findOne(id: UUID, participant: String): Uni<Event> = eventDao.findByIdForParticipant(id, participant)
        .invoke { event -> log.info("Retrieved event $event") }
        .onItem().ifNull().switchTo {
            eventDao.findById(id).toUni()
                .onItem().ifNull().failWith { NotFoundException("Event $id not found") }
                .map { it.copy(participant = participant) }
                .call { event -> eventDao.save(event) }
        }

    fun findAllForParticipant(participant: String): Multi<Event> = eventDao.findByParticipant(participant)
        .invoke { event -> log.info("Retrieved event $event (searching all for participant)") }
}
