import axios from "@/axios"

export default {

  getUserVotes: async (eventId) => await axios.get(`/api/votes/${eventId}`).then(response => response.data),

  getStatistics: async (eventId) => await axios.get(`/api/votes/${eventId}/stats`).then(response => response.data)
}
