import { render, screen } from '@testing-library/react'
import ParticipantList from '../../components/ParticipantList'

describe('ParticipantList', () => {
  it('renders without crashing', () => {
    render(<ParticipantList participants={[]} onAdd={() => {}} onToggle={() => {}} onDelete={() => {}} />)
    expect(screen.getByTestId('participant-list')).toBeInTheDocument()
  })

  it('displays participant names', () => {
    const participants = [{ id: 1, name: 'Alice', active: true }]
    render(<ParticipantList participants={participants} onAdd={() => {}} onToggle={() => {}} onDelete={() => {}} />)
    expect(screen.getByTestId('participant-list')).toHaveTextContent('Alice')
  })
})
