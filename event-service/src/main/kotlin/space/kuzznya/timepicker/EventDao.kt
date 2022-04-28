package space.kuzznya.timepicker

import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Delete
import com.datastax.oss.driver.api.mapper.annotations.Insert
import com.datastax.oss.driver.api.mapper.annotations.Select
import com.datastax.oss.driver.api.mapper.annotations.Update
import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import java.util.UUID

@Dao
interface EventDao {

    @Insert
    fun save(event: Event): Uni<Void>

    @Update
    fun update(event: Event): Uni<Void>

    @Delete(entityClass = [Event::class])
    fun delete(id: UUID, participant: String): Uni<Void>

    @Select
    fun findById(id: UUID, participant: String): Uni<Event>

    @Select(customWhereClause = "participant = :participant", allowFiltering = true)
    fun findByParticipant(participant: String): Multi<Event>
}
