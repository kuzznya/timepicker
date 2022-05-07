package space.kuzznya.timepicker.model

import java.time.LocalDate
import java.util.*

data class EventUpdate(
    val id: UUID,
    val title: String,
    val minDate: LocalDate,
    val maxDate: LocalDate
)
