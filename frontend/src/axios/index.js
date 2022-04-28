import axios from "axios"

const apiHost = process.env.VUE_APP_API_PATH || "API_PATH"

const instance = axios.create({baseURL: apiHost})

export default instance
