package space.kuzznya.timepicker

import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Insert
import com.datastax.oss.driver.api.mapper.annotations.Select
import com.datastax.oss.driver.api.mapper.annotations.Update
import com.datastax.oss.quarkus.runtime.api.reactive.mapper.MutinyMappedReactiveResultSet
import io.smallrye.mutiny.Uni

@Dao
interface EventDao {

    @Insert
    fun save(event: Event): Uni<Void>

    @Update
    fun update(event: Event): Uni<Void>

    @Select(customWhereClause = "participant = :participant", allowFiltering = true)
    fun findByParticipant(participant: String): MutinyMappedReactiveResultSet<Event>
}
