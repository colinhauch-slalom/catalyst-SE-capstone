import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Auth
export const register = (username, password) =>
  api.post('/auth/register', { username, password })

export const login = (username, password) =>
  api.post('/auth/login', { username, password })

// Participants
export const getParticipants = () =>
  api.get('/participants')

export const addParticipant = (name) =>
  api.post('/participants', { name })

export const toggleParticipant = (id, active) =>
  api.patch(`/participants/${id}`, { active })

export const deleteParticipant = (id) =>
  api.delete(`/participants/${id}`)

// Spin history
export const recordSpin = (pickedName) =>
  api.post('/spins', { pickedName })

export const getSpins = () =>
  api.get('/spins')
