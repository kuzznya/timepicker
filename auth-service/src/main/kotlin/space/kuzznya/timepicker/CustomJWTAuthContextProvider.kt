package space.kuzznya.timepicker

import io.quarkus.arc.Priority
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo
import io.smallrye.jwt.config.JWTAuthContextInfoProvider
import io.smallrye.jwt.util.KeyUtils
import org.eclipse.microprofile.config.inject.ConfigProperty
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Alternative
import javax.enterprise.inject.Produces

@Dependent
class CustomJWTAuthContextProvider(
    @ConfigProperty(name = "jwt.secret") private val jwtSecret: String,
    @ConfigProperty(name = "jwt.issued-by") private val issuedBy: String
) {

//    @Produces
//    @Alternative
//    @Priority(1)
//    fun jwtAuthContextProvider(originalProvider: JWTAuthContextInfoProvider) = JWTAuthContextInfo(originalProvider.contextInfo)
//        .also { it.secretVerificationKey = KeyUtils.createSecretKeyFromSecret(jwtSecret) }
}
