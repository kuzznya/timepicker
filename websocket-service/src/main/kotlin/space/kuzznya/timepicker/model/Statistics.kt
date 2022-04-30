package space.kuzznya.timepicker.model

import java.time.LocalDate
import java.util.UUID

data class Statistics(
    val eventId: UUID,
    val statistics: Map<LocalDate, Int>
)
