import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import RegisterPage from '../../pages/RegisterPage'
import { useAuth } from '../../context/AuthContext'
import { login, register } from '../../api/api'
import { useNavigate } from 'react-router-dom'

vi.mock('../../context/AuthContext', () => ({
  useAuth: vi.fn(),
}))

vi.mock('../../api/api', () => ({
  register: vi.fn(),
  login: vi.fn(),
}))

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: vi.fn(),
  }
})

describe('RegisterPage', () => {
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
        <RegisterPage />
      </MemoryRouter>
    )
  }

  async function fillForm(user, username, password, confirm = password) {
    await user.type(screen.getByPlaceholderText('choose a username'), username)
    await user.type(screen.getAllByPlaceholderText('••••••••')[0], password)
    await user.type(screen.getAllByPlaceholderText('••••••••')[1], confirm)
  }

  it('shows an error when passwords do not match', async () => {
    const user = userEvent.setup()

    renderPage()
    await fillForm(user, 'alice', 'secret123', 'different123')
    await user.click(screen.getByRole('button', { name: 'Create Account' }))

    expect(screen.getByText('Passwords do not match.')).toBeInTheDocument()
    expect(register).not.toHaveBeenCalled()
  })

  it('shows an error when the password is too short', async () => {
    const user = userEvent.setup()

    renderPage()
    await fillForm(user, 'alice', '12345')
    await user.click(screen.getByRole('button', { name: 'Create Account' }))

    expect(screen.getByText('Password must be at least 6 characters.')).toBeInTheDocument()
    expect(register).not.toHaveBeenCalled()
  })

  it('registers, logs in, and navigates home', async () => {
    const user = userEvent.setup()
    register.mockResolvedValue({})
    login.mockResolvedValue({ data: { token: 'jwt-token' } })

    renderPage()
    await fillForm(user, 'alice', 'secret123')
    await user.click(screen.getByRole('button', { name: 'Create Account' }))

    await waitFor(() => expect(register).toHaveBeenCalledWith('alice', 'secret123'))
    expect(login).toHaveBeenCalledWith('alice', 'secret123')
    expect(mockSaveToken).toHaveBeenCalledWith('jwt-token')
    expect(mockNavigate).toHaveBeenCalledWith('/')
  })

  it('shows a username taken error on conflict', async () => {
    const user = userEvent.setup()
    register.mockRejectedValue({ response: { status: 409 } })

    renderPage()
    await fillForm(user, 'alice', 'secret123')
    await user.click(screen.getByRole('button', { name: 'Create Account' }))

    expect(await screen.findByText('Username is taken.')).toBeInTheDocument()
  })

  it('shows a generic registration error for other failures', async () => {
    const user = userEvent.setup()
    register.mockRejectedValue(new Error('boom'))

    renderPage()
    await fillForm(user, 'alice', 'secret123')
    await user.click(screen.getByRole('button', { name: 'Create Account' }))

    expect(await screen.findByText('Registration failed.')).toBeInTheDocument()
  })
})