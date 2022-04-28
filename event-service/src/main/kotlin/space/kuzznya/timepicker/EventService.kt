package space.kuzznya.timepicker

import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class EventService {

    @Inject
    internal lateinit var eventDao: EventDao

    fun save(event: Event, author: String) = eventDao.save(
        event.copy(
            id = UUID.randomUUID(),
            participant = author,
            author = author
        )
    )

    fun delete(id: UUID, participant: String) = eventDao.delete(id, participant)

    fun findOne(id: UUID, participant: String) = eventDao.findById(id, participant)

    fun findAllForParticipant(participant: String) = eventDao.findByParticipant(participant)
}
