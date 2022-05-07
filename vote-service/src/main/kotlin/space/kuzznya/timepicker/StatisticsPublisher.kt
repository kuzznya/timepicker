package space.kuzznya.timepicker

import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.future.await
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class StatisticsPublisher(
    @Channel("statistics")
    private val statsEmitter: Emitter<Statistics>,
    private val voteDao: VoteDao
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(StatisticsPublisher::class.java)
    }

    suspend fun publishStats(event: UUID) {
        val stats = calculateStats(event)
        log.info("Statistics calculated: $stats")
        statsEmitter.send(stats).await()
    }

    suspend fun calculateStats(event: UUID): Statistics {
        val votes = voteDao.findAllForEvent(event).collect().asList().awaitSuspending()
        return Statistics(event, votes.groupingBy { it.date }.eachCount())
    }
}
