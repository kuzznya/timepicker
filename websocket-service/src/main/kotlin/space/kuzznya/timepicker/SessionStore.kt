package space.kuzznya.timepicker

import io.quarkus.redis.client.reactive.ReactiveRedisClient
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SessionStore(
    private val redisClient: ReactiveRedisClient,
    @ConfigProperty(name = "ws.session.timeout", defaultValue = "60")
    private val sessionTimeout: Int
) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(SessionStore::class.java)
    }

    suspend fun createSession(username: String): UUID {
        val id = UUID.randomUUID()
        redisClient.set(listOf(id.toString(), username)).awaitSuspending()
        redisClient.expire(id.toString(), sessionTimeout.toString()).awaitSuspending()
        log.info("Session $id created, timeout $sessionTimeout seconds")
        return id
    }

    suspend fun useSession(id: UUID): String? {
        val username = redisClient.get(id.toString()).awaitSuspending()?.toString()
        redisClient.del(listOf(id.toString())).awaitSuspending()
        if (username != null) log.info("Session $id is used, data deleted from Redis")
        return username
    }
}
