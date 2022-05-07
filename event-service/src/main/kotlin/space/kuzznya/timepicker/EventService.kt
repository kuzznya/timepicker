package space.kuzznya.timepicker

import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.future.await
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.NotFoundException

@ApplicationScoped
class EventService(
    private val eventDao: EventDao,
    @Channel("events")
    private val eventEmitter: Emitter<EventUpdate>
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(EventService::class.java)
    }

    suspend fun save(event: Event, author: String) {
        val newEvent = event.copy(
            id = UUID.randomUUID(),
            participant = author,
            author = author
        )
        if (newEvent.minDate > newEvent.maxDate) throw RuntimeException("Min date cannot be more than max date")
        log.info("Creating event $newEvent")
        eventDao.save(newEvent).awaitSuspending()
        eventEmitter.send(EventUpdate(newEvent, EventUpdateStatus.CREATED)).await()
    }

    suspend fun updateDates(id: UUID, user: String, request: DateUpdateRequest): Event {
        val participantEvent: Event = eventDao.findByIdForParticipant(id, user)
            .onItem().ifNull().failWith { NotFoundException("Event $id not found") }
            .awaitSuspending()
        eventDao.findById(id)
            .map { it.copy(minDate = request.minDate, maxDate = request.maxDate) }
            .call { event -> eventDao.save(event) }
            .collect().asList().awaitSuspending()
        val eventUpdate = EventUpdate(
            participantEvent.copy(minDate = request.minDate, maxDate = request.maxDate),
            EventUpdateStatus.UPDATED
        )
        eventEmitter.send(eventUpdate).await()
        return participantEvent
    }

    suspend fun delete(id: UUID, participant: String) = eventDao.delete(id, participant)
        .awaitSuspending()
        .also { log.info("Event $id, participant $participant deleted") }

    suspend fun findOne(id: UUID, participant: String): Event =
        eventDao.findByIdForParticipant(id, participant)
            .invoke { event -> log.info("Retrieved event $event") }
            .onItem().ifNull().switchTo {
                eventDao.findById(id).toUni()
                    .onItem().ifNull().failWith { NotFoundException("Event $id not found") }
                    .map { it.copy(participant = participant) }
                    .call { event -> eventDao.save(event) }
            }.awaitSuspending()

    suspend fun findAllForParticipant(participant: String): List<Event> =
        eventDao.findByParticipant(participant)
            .invoke { event -> log.info("Retrieved event $event (searching all for participant)") }
            .collect().asList().awaitSuspending()
}
