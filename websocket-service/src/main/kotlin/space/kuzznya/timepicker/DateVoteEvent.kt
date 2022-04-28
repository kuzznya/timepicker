package space.kuzznya.timepicker

import java.time.LocalDate
import java.util.UUID

data class DateVoteEvent(
    val username: String,
    val eventId: UUID,
    val date: LocalDate,
    val state: DateVoteState
)