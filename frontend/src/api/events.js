import axios from "@/axios"

export default {

  addEvent: async (data) => await axios.post("/api/events", data).then(response => response.data),

  getEvents: async () => await axios.get("/api/events").then(response => response.data),

  getEventInfo: async (id) => await axios.get(`/api/events/${id}`).then(response => response.data),

  deleteEvent: async (id) => await axios.delete(`/api/events/${id}`).then(response => response.data)
}
