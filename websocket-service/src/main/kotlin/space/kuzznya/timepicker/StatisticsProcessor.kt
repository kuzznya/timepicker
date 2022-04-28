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
        println(stats)
        webSocket.sessions.values
            .filter { it.eventId == stats.eventId }
            .filter { it.session.isOpen }
            .onEach { println("Sending real-time stats to ${it.username}") }
            .forEach { it.session.asyncRemote.sendText(data.encode()) }
    }
}
