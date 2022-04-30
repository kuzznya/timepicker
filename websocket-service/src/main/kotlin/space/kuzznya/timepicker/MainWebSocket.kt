package space.kuzznya.timepicker

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.smallrye.reactivemessaging.runtime.kotlin.VertxDispatcher
import io.vertx.core.Vertx
import kotlinx.coroutines.*
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import space.kuzznya.timepicker.ws.DateVoteDecoder
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@ServerEndpoint("/api/ws/{id}/{event}", decoders = [DateVoteDecoder::class])
@ApplicationScoped
class MainWebSocket(
    @Channel("votes")
    private val emitter: Emitter<DateVoteEvent>,
    private val sessionStore: SessionStore,
    private val vertx: Vertx
) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(MainWebSocket::class.java)
    }

    val sessions: MutableMap<UUID, SessionInfo> = mutableMapOf()

    private val mapper = ObjectMapper().findAndRegisterModules()

    @OnOpen
    fun onOpen(session: Session, @PathParam("id") idParam: String, @PathParam("event") eventIdParam: String) {
        val id = UUID.fromString(idParam)
        val eventId = UUID.fromString(eventIdParam)
        CoroutineScope(VertxDispatcher(vertx.orCreateContext)).launch {
            val username = sessionStore.useSession(id)
            if (username == null) {
                log.warn("Unknown session id $id")
                withContext(Dispatchers.IO) {
                    session.close(CloseReason(CloseReason.CloseCodes.NO_STATUS_CODE, "Unknown session id $id"))
                    log.info("Session closed")
                }
                return@launch
            }
            sessions[id] = SessionInfo(username, eventId, session)
            log.info("Client $username connected")
        }
    }

    @OnClose
    fun onClose(session: Session, @PathParam("id") idParam: String) {
        val id = UUID.fromString(idParam)
        sessions.remove(id)
        log.info("Session $id is closed")
    }

    @OnError
    fun onError(session: Session, throwable: Throwable) {
        log.error("WebSocket error occurred", throwable)
        throwable.printStackTrace()
    }

    @OnMessage
    fun onMessage(session: Session, @PathParam("id") idParam: String, data: String) {
        val id = UUID.fromString(idParam)
        val message = mapper.readValue(data, DateVoteMessage::class.java)
        log.info("Received vote message $message")
        val username = sessions[id]?.username ?: throw RuntimeException("Session not found")
        emitter.send(DateVoteEvent(username, message.event, message.date, message.state))
    }
}
