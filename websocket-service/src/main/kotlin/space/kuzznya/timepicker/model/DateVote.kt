package space.kuzznya.timepicker.model

import java.time.LocalDate
import java.util.UUID

data class DateVote(
    val username: String,
    val eventId: UUID,
    val date: LocalDate,
    val state: DateVoteState
)
