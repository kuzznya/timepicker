package space.kuzznya.timepicker

import io.quarkus.vertx.http.runtime.security.HttpAuthenticator
import org.jboss.logmanager.Logger
import javax.annotation.PostConstruct
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugAuthenticator {

    @Inject
    private lateinit var httpAuthenticator: HttpAuthenticator

    @PostConstruct
    fun init() {
        val mechanismsField = httpAuthenticator::class.java.getDeclaredField("mechanisms")
        mechanismsField.isAccessible = true
        println("AAAA")
        println(mechanismsField.get(httpAuthenticator))
    }
}
