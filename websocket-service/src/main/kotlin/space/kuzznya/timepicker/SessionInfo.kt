package space.kuzznya.timepicker

import java.util.UUID
import javax.websocket.Session

data class SessionInfo(
    val username: String,
    val eventId: UUID,
    val session: Session
)
