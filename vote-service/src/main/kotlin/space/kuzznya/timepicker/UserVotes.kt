package space.kuzznya.timepicker

import java.time.LocalDate
import java.util.UUID

data class UserVotes(
    val eventId: UUID,
    val username: String,
    val votes: List<LocalDate>
)
