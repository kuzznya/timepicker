package space.kuzznya.timepicker

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class EventService {

    @Inject
    internal lateinit var eventDao: EventDao

    fun save(event: Event, author: String) = eventDao.save(event.copy(participant = author, author = author))

    fun findAllForParticipant(participant: String) = eventDao.findByParticipant(participant).also { println(participant) }
}
