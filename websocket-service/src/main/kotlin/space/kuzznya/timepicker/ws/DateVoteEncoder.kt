package space.kuzznya.timepicker.ws

import com.fasterxml.jackson.databind.ObjectMapper
import space.kuzznya.timepicker.DateVoteMessage
import javax.websocket.Encoder
import javax.websocket.EndpointConfig

class DateVoteEncoder : Encoder.Text<DateVoteMessage> {

    private val mapper = ObjectMapper()

    override fun init(config: EndpointConfig?) { }

    override fun destroy() { }

    override fun encode(message: DateVoteMessage?): String {
        return mapper.writeValueAsString(message)
    }

}
