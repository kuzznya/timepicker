package space.kuzznya.timepicker

import io.vertx.core.json.JsonObject
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.kuzznya.timepicker.model.MessageType
import space.kuzznya.timepicker.model.Statistics
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StatisticsProcessor(
    private val ws: MainWebSocket
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(StatisticsProcessor::class.java)
    }

    @Incoming("statistics")
    fun process(data: JsonObject) {
        val stats = data.mapTo(Statistics::class.java)
        log.info("Received statistics: $stats")
        val typedData = data.put("type", MessageType.STATISTICS)
        ws.sessions.values
            .filter { it.eventId == stats.eventId }
            .filter { it.session.isOpen }
            .onEach { log.info("Sending real-time stats of event ${it.eventId} to ${it.username} " +
                "(session ${it.session.pathParameters["id"]}") }
            .forEach { it.session.asyncRemote.sendText(typedData.encode()) }
    }
}
