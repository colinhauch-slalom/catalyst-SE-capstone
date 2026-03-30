import { render, screen, waitFor } from '@testing-library/react'
import App from '../App'

vi.mock('../pages/LoginPage', () => ({
  default: () => <div>Login Screen</div>,
}))

vi.mock('../pages/RegisterPage', () => ({
  default: () => <div>Register Screen</div>,
}))

vi.mock('../pages/MainPage', () => ({
  default: () => <div>Main Screen</div>,
}))

describe('App routing', () => {
  beforeEach(() => {
    localStorage.clear()
    window.history.pushState({}, '', '/')
  })

  it('redirects unauthenticated users from home to login', async () => {
    render(<App />)

    await waitFor(() => expect(screen.getByText('Login Screen')).toBeInTheDocument())
  })

  it('redirects authenticated users away from login to home', async () => {
    localStorage.setItem('token', 'jwt-token')
    window.history.pushState({}, '', '/login')

    render(<App />)

    await waitFor(() => expect(screen.getByText('Main Screen')).toBeInTheDocument())
  })

  it('renders the register page for unauthenticated users', async () => {
    window.history.pushState({}, '', '/register')

    render(<App />)

    await waitFor(() => expect(screen.getByText('Register Screen')).toBeInTheDocument())
  })
})