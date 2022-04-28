package space.kuzznya.timepicker

import com.datastax.oss.driver.api.mapper.annotations.Entity
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy
import java.util.UUID

@Entity
@PropertyStrategy(mutable = false)
data class Event(
    @PartitionKey(1)
    val participant: String?,
    @PartitionKey(2)
    val eventId: UUID,
    val title: String,
    val author: String?
)
