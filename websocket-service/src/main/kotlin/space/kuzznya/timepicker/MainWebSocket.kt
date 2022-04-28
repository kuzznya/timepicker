package space.kuzznya.timepicker

import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.smallrye.reactivemessaging.runtime.kotlin.VertxDispatcher
import io.vertx.core.Vertx
import kotlinx.coroutines.*
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import space.kuzznya.timepicker.ws.DateVoteDecoder
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@ServerEndpoint("/ws/{id}/{event}", decoders = [DateVoteDecoder::class])
@ApplicationScoped
class MainWebSocket(
    @Channel("votes")
    private val emitter: Emitter<DateVoteEvent>,
    private val sessionStore: SessionStore,
    private val vertx: Vertx
) {

    val sessions: MutableMap<UUID, SessionInfo> = mutableMapOf()

    private val mapper = ObjectMapper().findAndRegisterModules()

    @OnOpen
    fun onOpen(session: Session, @PathParam("id") idParam: String, @PathParam("event") eventIdParam: String) {
        val id = UUID.fromString(idParam)
        val eventId = UUID.fromString(eventIdParam)
        CoroutineScope(VertxDispatcher(vertx.orCreateContext)).launch {
            val username = sessionStore.useSession(id)
            if (username == null) {
                println("Unknown session id")
                withContext(Dispatchers.IO) {
                    session.close(CloseReason(CloseReason.CloseCodes.NO_STATUS_CODE, "Unknown session id"))
                }
                return@launch
            }
            sessions[id] = SessionInfo(username, eventId, session)
            println("Client $username connected")
        }
    }

    @OnClose
    fun onClose(session: Session, @PathParam("id") idParam: String) {
        val id = UUID.fromString(idParam)
        sessions.remove(id)
    }

    @OnError
    fun onError(session: Session, throwable: Throwable) {
        println("Fuck")
        throwable.printStackTrace()
    }

    @OnMessage
    fun onMessage(session: Session, @PathParam("id") idParam: String, data: String) {
        val id = UUID.fromString(idParam)
        val message = mapper.readValue(data, DateVoteMessage::class.java)
        println(message)
        val username = sessions[id]?.username ?: throw RuntimeException("Session not found")
        emitter.send(DateVoteEvent(username, message.event, message.date, message.state))
    }
}
