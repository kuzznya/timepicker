package space.kuzznya.timepicker

import io.vertx.core.json.JsonObject
import org.eclipse.microprofile.reactive.messaging.Incoming
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.kuzznya.timepicker.model.UserVotes
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserVotesProcessor(
    private val ws: MainWebSocket
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserVotesProcessor::class.java)
    }

    @Incoming("user-votes")
    fun process(data: JsonObject) {
        val userVotes = data.mapTo(UserVotes::class.java)
        ws.sessions.values
            .filter { it.eventId == userVotes.eventId && it.username == userVotes.username }
            .filter { it.session.isOpen }
            .onEach { log.info("Sending user's votes of event ${it.eventId} to ${it.username} " +
                "(session ${it.session.pathParameters["id"]}") }
            .forEach { it.session.asyncRemote.sendText(data.encode()) }
    }
}
