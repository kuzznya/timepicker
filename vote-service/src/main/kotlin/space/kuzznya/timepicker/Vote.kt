package space.kuzznya.timepicker

import com.datastax.oss.driver.api.mapper.annotations.Entity
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy
import java.time.LocalDate
import java.util.UUID

@Entity
@PropertyStrategy(mutable = false)
data class Vote(
    @PartitionKey(1)
    val eventId: UUID,
    @PartitionKey(2)
    val username: String,
    @PartitionKey(3)
    val date: LocalDate,
) {
    @com.datastax.oss.driver.api.mapper.annotations.Transient
    var state: VoteState = VoteState.VOTED
}
