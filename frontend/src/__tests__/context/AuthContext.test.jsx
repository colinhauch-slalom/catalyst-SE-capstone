import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { AuthProvider, useAuth } from '../../context/AuthContext'

function AuthConsumer() {
  const { token, login, logout } = useAuth()

  return (
    <div>
      <span data-testid="token-value">{token ?? 'none'}</span>
      <button onClick={() => login('new-token')}>login</button>
      <button onClick={logout}>logout</button>
    </div>
  )
}

describe('AuthContext', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('initializes the token from localStorage', () => {
    localStorage.setItem('token', 'stored-token')

    render(
      <AuthProvider>
        <AuthConsumer />
      </AuthProvider>
    )

    expect(screen.getByTestId('token-value')).toHaveTextContent('stored-token')
  })

  it('stores a token when login is called', async () => {
    const user = userEvent.setup()

    render(
      <AuthProvider>
        <AuthConsumer />
      </AuthProvider>
    )

    await user.click(screen.getByRole('button', { name: 'login' }))

    expect(localStorage.getItem('token')).toBe('new-token')
    expect(screen.getByTestId('token-value')).toHaveTextContent('new-token')
  })

  it('clears the token when logout is called', async () => {
    const user = userEvent.setup()
    localStorage.setItem('token', 'stored-token')

    render(
      <AuthProvider>
        <AuthConsumer />
      </AuthProvider>
    )

    await user.click(screen.getByRole('button', { name: 'logout' }))

    expect(localStorage.getItem('token')).toBeNull()
    expect(screen.getByTestId('token-value')).toHaveTextContent('none')
  })
})