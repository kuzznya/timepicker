package space.kuzznya.timepicker

import org.eclipse.microprofile.jwt.JsonWebToken
import java.util.*
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/sessions")
class WebSocketSessionResource(
    private val sessionStore: SessionStore
) {

    @Inject
    private lateinit var accessToken: JsonWebToken

    @POST
    suspend fun generateSessionId(): UUID = sessionStore.createSession(accessToken.name)
}
