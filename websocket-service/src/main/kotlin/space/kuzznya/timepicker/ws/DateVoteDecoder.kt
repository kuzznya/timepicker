package space.kuzznya.timepicker.ws

import com.fasterxml.jackson.databind.ObjectMapper
import space.kuzznya.timepicker.DateVoteMessage
import javax.websocket.Decoder
import javax.websocket.EndpointConfig

class DateVoteDecoder : Decoder.Text<DateVoteMessage> {

    private val mapper = ObjectMapper()

    override fun init(config: EndpointConfig?) { }

    override fun destroy() { }

    override fun decode(s: String?): DateVoteMessage = mapper.readValue(s, DateVoteMessage::class.java)

    override fun willDecode(s: String?): Boolean =
        kotlin.runCatching { mapper.readValue(s, DateVoteMessage::class.java) }
            .map { true }
            .getOrElse { false }
}
