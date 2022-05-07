package space.kuzznya.timepicker

import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Delete
import com.datastax.oss.driver.api.mapper.annotations.Insert
import com.datastax.oss.driver.api.mapper.annotations.Select
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import java.time.LocalDate
import java.util.UUID

@Dao
interface VoteDao {

    @Insert(ifNotExists = true)
    fun save(vote: Vote): Uni<Void>

    @Delete
    fun delete(vote: Vote): Uni<Void>

    @Select(customWhereClause = "event_id = :eventId", allowFiltering = true)
    fun findAllForEvent(eventId: UUID): Multi<Vote>

    @Select(customWhereClause = "username = :username AND event_id = :eventId", allowFiltering = true)
    fun findAllForUserAndEvent(username: String, eventId: UUID): Multi<Vote>

    @Select(customWhereClause = "event_id = :eventId AND date < :minDate", allowFiltering = true)
    fun findAllBefore(eventId: UUID, minDate: LocalDate): Multi<Vote>

    @Select(customWhereClause = "event_id = :eventId AND date > :maxDate", allowFiltering = true)
    fun findAllAfter(eventId: UUID, maxDate: LocalDate): Multi<Vote>
}
