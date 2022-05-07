package space.kuzznya.timepicker.event

import java.time.LocalDate
import java.util.*

data class EventUpdate(
    val id: UUID,
    val minDate: LocalDate,
    val maxDate: LocalDate
)
