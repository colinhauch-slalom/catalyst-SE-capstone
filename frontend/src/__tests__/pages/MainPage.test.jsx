import { act, fireEvent, render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import MainPage from '../../pages/MainPage'
import { useAuth } from '../../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import {
  addParticipant,
  deleteParticipant,
  getParticipants,
  getSpins,
  recordSpin,
  toggleParticipant,
} from '../../api/api'
import { pickWinner } from '../../utils/wheelUtils'

vi.mock('../../context/AuthContext', () => ({
  useAuth: vi.fn(),
}))

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: vi.fn(),
  }
})

vi.mock('../../api/api', () => ({
  getParticipants: vi.fn(),
  addParticipant: vi.fn(),
  toggleParticipant: vi.fn(),
  deleteParticipant: vi.fn(),
  recordSpin: vi.fn(),
  getSpins: vi.fn(),
}))

vi.mock('../../utils/wheelUtils', () => ({
  pickWinner: vi.fn(),
}))

describe('MainPage', () => {
  const mockLogout = vi.fn()
  const mockNavigate = vi.fn()

  beforeEach(() => {
    vi.clearAllMocks()
    vi.useRealTimers()
    useAuth.mockReturnValue({ logout: mockLogout })
    useNavigate.mockReturnValue(mockNavigate)
    getParticipants.mockResolvedValue({
      data: [
        { id: 1, name: 'Alice', active: true },
        { id: 2, name: 'Bob', active: true },
      ],
    })
    getSpins.mockResolvedValue({
      data: [{ id: 10, pickedName: 'Alice', spunAt: '2026-03-23T10:00:00Z' }],
    })
    addParticipant.mockResolvedValue({ data: { id: 3, name: 'Cara', active: true } })
    toggleParticipant.mockResolvedValue({})
    deleteParticipant.mockResolvedValue({})
    recordSpin.mockResolvedValue({
      data: { id: 11, pickedName: 'Bob', spunAt: '2026-03-30T10:00:00Z' },
    })
    pickWinner.mockImplementation((participants) => participants[1])
  })

  it('loads participants and toggles history view', async () => {
    const user = userEvent.setup()

    render(<MainPage />)

    expect(await screen.findByText('Alice')).toBeInTheDocument()
    expect(screen.getByText('Bob')).toBeInTheDocument()
    expect(getParticipants).toHaveBeenCalledTimes(1)
    expect(getSpins).toHaveBeenCalledTimes(1)

    await user.click(screen.getByRole('button', { name: 'History' }))

    expect(screen.getByTestId('spin-history')).toBeInTheDocument()
    expect(screen.getByText('Spin History')).toBeInTheDocument()

    await user.click(screen.getByRole('button', { name: '✕' }))

    expect(screen.getByTestId('participant-list')).toBeInTheDocument()
  })

  it('marks participant changes dirty and saves them', async () => {
    const user = userEvent.setup()

    render(<MainPage />)

    expect(await screen.findByLabelText('Toggle Alice')).toBeChecked()
    expect(screen.getByRole('button', { name: 'SPIN' })).toBeEnabled()

    await user.click(screen.getByLabelText('Toggle Alice'))

    expect(screen.getByRole('button', { name: 'Save changes' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Add 2+ people' })).toBeDisabled()

    await user.click(screen.getByRole('button', { name: 'Save changes' }))

    await waitFor(() => expect(toggleParticipant).toHaveBeenCalledWith(1, false))
    await waitFor(() => {
      expect(screen.queryByRole('button', { name: 'Save changes' })).not.toBeInTheDocument()
    })
  })

  it('adds and deletes participants', async () => {
    const user = userEvent.setup()

    render(<MainPage />)

    expect(await screen.findByText('Alice')).toBeInTheDocument()

    await user.type(screen.getByPlaceholderText('Add a name…'), 'Cara')
    await user.click(screen.getByRole('button', { name: '+' }))

    await waitFor(() => expect(addParticipant).toHaveBeenCalledWith('Cara'))
    expect(await screen.findByText('Cara')).toBeInTheDocument()

    const removeButtons = screen.getAllByTitle('Remove')
    await user.click(removeButtons[0])

    await waitFor(() => expect(deleteParticipant).toHaveBeenCalledWith(1))
    expect(screen.queryByText('Alice')).not.toBeInTheDocument()
  })

  it('shows an error when adding a participant fails', async () => {
    const user = userEvent.setup()
    addParticipant.mockRejectedValueOnce(new Error('nope'))

    render(<MainPage />)

    expect(await screen.findByText('Alice')).toBeInTheDocument()
    await user.type(screen.getByPlaceholderText('Add a name…'), 'Cara')
    await user.click(screen.getByRole('button', { name: '+' }))

    expect(await screen.findByText('Failed to add participant.')).toBeInTheDocument()
  })

  it('spins, records the winner, and logs out', async () => {
    render(<MainPage />)

    expect(await screen.findByText('Alice')).toBeInTheDocument()

    vi.useFakeTimers()

    fireEvent.click(screen.getByRole('button', { name: 'SPIN' }))

    expect(pickWinner).toHaveBeenCalledTimes(1)
    expect(screen.getByRole('button', { name: 'Spinning…' })).toBeDisabled()

    await act(async () => {
      vi.advanceTimersByTime(4200)
      await Promise.resolve()
    })

    expect(recordSpin).toHaveBeenCalledWith('Bob')
    expect(screen.getByText('DOOMED')).toBeInTheDocument()
    expect(screen.getByText('Bob', { selector: '.winner-name' })).toBeInTheDocument()

    fireEvent.click(screen.getByRole('button', { name: 'History' }))
    expect(screen.getByTestId('spin-history')).toHaveTextContent('Bob')

    fireEvent.click(screen.getByRole('button', { name: 'Sign Out' }))
    expect(mockLogout).toHaveBeenCalledTimes(1)
    expect(mockNavigate).toHaveBeenCalledWith('/login')

    vi.useRealTimers()
  })
})