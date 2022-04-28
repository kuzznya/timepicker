package space.kuzznya.timepicker

import io.vertx.core.json.JsonObject
import org.eclipse.microprofile.reactive.messaging.Incoming
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StatisticsProcessor(
    private val webSocket: MainWebSocket
) {

    @Incoming("statistics")
    fun process(data: JsonObject) {
        val stats = data.mapTo(Statistics::class.java)
        webSocket.sessions.values
            .filter { it.eventId == stats.eventId }
            .filter { it.session.isOpen }
            .forEach { it.session.asyncRemote.sendText(data.encode()) }
    }
}
