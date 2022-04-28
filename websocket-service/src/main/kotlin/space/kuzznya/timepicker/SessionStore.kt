package space.kuzznya.timepicker

import io.quarkus.redis.client.reactive.ReactiveRedisClient
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.UUID
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SessionStore(
    private val redisClient: ReactiveRedisClient,
    @ConfigProperty(name = "ws.session.timeout", defaultValue = "60")
    private val sessionTimeout: Int
) {

    suspend fun createSession(username: String): UUID {
        val id = UUID.randomUUID()
        redisClient.set(listOf(id.toString(), username)).awaitSuspending()
        redisClient.expire(id.toString(), sessionTimeout.toString()).awaitSuspending()
        return id
    }

    suspend fun useSession(id: UUID): String? {
        val username = redisClient.get(id.toString()).awaitSuspending()?.toString()
        redisClient.del(listOf(id.toString())).awaitSuspending()
        return username
    }
}
