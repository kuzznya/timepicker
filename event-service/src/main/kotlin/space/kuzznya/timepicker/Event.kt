package space.kuzznya.timepicker

import com.datastax.oss.driver.api.mapper.annotations.Entity
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy
import java.time.LocalDate
import java.util.UUID

@Entity
@PropertyStrategy(mutable = false)
data class Event(
    @PartitionKey(1)
    val id: UUID?,
    @PartitionKey(2)
    val participant: String?,
    val title: String,
    val author: String?,
    val minDate: LocalDate,
    val maxDate: LocalDate
)
