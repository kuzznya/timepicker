package space.kuzznya.timepicker

import io.quarkus.oidc.IdToken
import io.quarkus.oidc.UserInfo
import io.quarkus.security.Authenticated
import io.smallrye.jwt.algorithm.SignatureAlgorithm
import io.smallrye.jwt.build.Jwt
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.JsonWebToken
import java.net.URI
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response

@Path("/auth")
class AuthResource(
    @ConfigProperty(name =  "jwt.secret") private val jwtSecret: String
) {

    @Inject
    internal lateinit var accessToken: JsonWebToken
    @Inject
    @IdToken
    internal lateinit var idToken: JsonWebToken
    @Inject
    internal lateinit var userInfo: UserInfo

    @GET
    @Path("/login")
    @Authenticated
    fun login(@QueryParam("redirect_uri") redirectUri: String): Response =
        Response.seeOther(URI.create(redirectUri))
            .cookie(createJwtCookie())
            .build()

    private fun createJwtCookie() = NewCookie(
        "JWT",
        createJwt(),
        "/",
        "localhost",
        "",
        3600,
        false, // fixme
        true
    )

    private fun createJwt(): String = Jwt.upn("test")
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plus(12, ChronoUnit.HOURS))
        .jws()
        .algorithm(SignatureAlgorithm.HS256)
        .signWithSecret(jwtSecret)
}
