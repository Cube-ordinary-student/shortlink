import axios from 'axios'
import { getToken, getUsername } from '@/core/auth.js'

const baseURL = '/api/short-link/admin/v1'

const http = axios.create({
  baseURL: baseURL,
  timeout: 15000
})

http.interceptors.request.use(
  (config) => {
    config.headers.Token = getToken() || ''
    config.headers.Username = getUsername() || ''
    return config
  },
  (error) => Promise.reject(error)
)

http.interceptors.response.use(
  (res) => {
    if (res.status === 200) {
      return Promise.resolve(res)
    }
    return Promise.reject(res)
  },
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

export default http
