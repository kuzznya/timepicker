import axios from "@/axios"

const apiHost = process.env.VUE_APP_API_PATH

export default {

  async setupWebSocket(eventId) {
    const sessionId = await axios.post("/api/ws/sessions").then(result => result.data)
    const wsPath = apiHost.replace(/^http/, "ws")
    return new WebSocket(`${wsPath}/api/ws/${sessionId}/${eventId}`)
  }
}
