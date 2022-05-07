package space.kuzznya.timepicker

import com.datastax.oss.driver.api.mapper.annotations.DaoFactory
import com.datastax.oss.driver.api.mapper.annotations.Mapper

@Mapper
interface EventMapper {
    @DaoFactory
    fun reactiveVoteDao(): VoteDao
}
