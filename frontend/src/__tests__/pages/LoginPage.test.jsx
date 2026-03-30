import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import LoginPage from '../../pages/LoginPage'
import { useAuth } from '../../context/AuthContext'
import { login } from '../../api/api'
import { useNavigate } from 'react-router-dom'

vi.mock('../../context/AuthContext', () => ({
  useAuth: vi.fn(),
}))

vi.mock('../../api/api', () => ({
  login: vi.fn(),
}))

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: vi.fn(),
  }
})

describe('LoginPage', () => {
  const mockSaveToken = vi.fn()
  const mockNavigate = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
    useAuth.mockReturnValue({ login: mockSaveToken })
    useNavigate.mockReturnValue(mockNavigate)
  })

  function renderPage() {
    return render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>
    )
  }

  it('logs in successfully and navigates home', async () => {
    const user = userEvent.setup()
    login.mockResolvedValue({ data: { token: 'jwt-token' } })

    renderPage()

    await user.type(screen.getByPlaceholderText('your username'), 'alice')
    await user.type(screen.getByPlaceholderText('••••••••'), 'secret123')
    await user.click(screen.getByRole('button', { name: 'Sign In' }))

    await waitFor(() => expect(login).toHaveBeenCalledWith('alice', 'secret123'))
    expect(mockSaveToken).toHaveBeenCalledWith('jwt-token')
    expect(mockNavigate).toHaveBeenCalledWith('/')
  })

  it('shows an error when login fails', async () => {
    const user = userEvent.setup()
    login.mockRejectedValue(new Error('bad credentials'))

    renderPage()

    await user.type(screen.getByPlaceholderText('your username'), 'alice')
    await user.type(screen.getByPlaceholderText('••••••••'), 'wrong-pass')
    await user.click(screen.getByRole('button', { name: 'Sign In' }))

    expect(await screen.findByText('Invalid username or password.')).toBeInTheDocument()
  })

  it('shows a loading label while signing in', async () => {
    const user = userEvent.setup()
    let resolveLogin
    login.mockReturnValue(new Promise((resolve) => {
      resolveLogin = resolve
    }))

    renderPage()

    await user.type(screen.getByPlaceholderText('your username'), 'alice')
    await user.type(screen.getByPlaceholderText('••••••••'), 'secret123')
    await user.click(screen.getByRole('button', { name: 'Sign In' }))

    expect(screen.getByRole('button', { name: 'Signing in…' })).toBeDisabled()

    resolveLogin({ data: { token: 'jwt-token' } })

    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith('/'))
  })
})