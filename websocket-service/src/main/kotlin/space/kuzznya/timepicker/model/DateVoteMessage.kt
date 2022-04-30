package space.kuzznya.timepicker.model

import java.time.LocalDate
import java.util.UUID

data class DateVoteMessage(
    val event: UUID,
    val date: LocalDate,
    val state: DateVoteState
)
