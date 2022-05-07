package space.kuzznya.timepicker.event

import com.datastax.oss.driver.api.mapper.annotations.Dao
import com.datastax.oss.driver.api.mapper.annotations.Insert
import com.datastax.oss.driver.api.mapper.annotations.Select
import io.smallrye.mutiny.Uni
import java.util.UUID

@Dao
interface EventDao {

    @Insert
    fun save(event: Event): Uni<Void>

    @Select
    fun findById(id: UUID): Uni<Event>
}
