package space.kuzznya.timepicker

import com.datastax.oss.driver.api.mapper.annotations.Entity
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy
import java.time.LocalDate
import java.util.UUID

@Entity
@PropertyStrategy(mutable = false)
data class Event(
    @PartitionKey
    val id: UUID,
    val minDate: LocalDate,
    val maxDate: LocalDate
)
