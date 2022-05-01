package space.kuzznya.timepicker

import java.time.LocalDate

data class DateUpdateRequest(
    val minDate: LocalDate,
    val maxDate: LocalDate
)
