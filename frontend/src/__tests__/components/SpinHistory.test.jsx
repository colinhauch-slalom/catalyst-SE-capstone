import { render, screen } from '@testing-library/react'
import SpinHistory from '../../components/SpinHistory'

describe('SpinHistory', () => {
  it('renders without crashing', () => {
    render(<SpinHistory history={[]} onClose={() => {}} />)
    expect(screen.getByTestId('spin-history')).toBeInTheDocument()
  })

  it('displays picked names', () => {
    const history = [{ id: 1, pickedName: 'Alice', spunAt: '2026-03-23T10:00:00' }]
    render(<SpinHistory history={history} onClose={() => {}} />)
    expect(screen.getByTestId('spin-history')).toHaveTextContent('Alice')
  })
})
