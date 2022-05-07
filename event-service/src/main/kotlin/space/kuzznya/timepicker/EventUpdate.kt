package space.kuzznya.timepicker

import java.time.LocalDate
import java.util.*

data class EventUpdate(
    val id: UUID?,
    val title: String,
    val author: String?,
    val minDate: LocalDate,
    val maxDate: LocalDate,
    val status: EventUpdateStatus
) {
    constructor(event: Event, status: EventUpdateStatus) : this(
        event.id,
        event.title,
        event.author,
        event.minDate,
        event.maxDate,
        status
    )
}

enum class EventUpdateStatus {
    CREATED,
    UPDATED
}
