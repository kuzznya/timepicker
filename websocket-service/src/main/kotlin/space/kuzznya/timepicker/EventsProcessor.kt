package space.kuzznya.timepicker

import io.vertx.core.json.JsonObject
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.kuzznya.timepicker.model.EventUpdate
import space.kuzznya.timepicker.model.MessageType
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EventsProcessor(
    private val ws: MainWebSocket
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(EventsProcessor::class.java)
    }

    @Incoming("events")
    fun process(data: JsonObject) {
        val eventUpdate = data.mapTo(EventUpdate::class.java)
        log.info("Received event update: $eventUpdate")
        val typedData = data.put("type", MessageType.EVENT_UPDATE)
        ws.sessions.values
            .filter { it.eventId == eventUpdate.id }
            .filter { it.session.isOpen }
            .onEach { log.info("Sending event ${it.eventId} update to ${it.username} " +
                "(session ${it.session.pathParameters["id"]}") }
            .forEach { it.session.asyncRemote.sendText(typedData.encode()) }
    }
}
