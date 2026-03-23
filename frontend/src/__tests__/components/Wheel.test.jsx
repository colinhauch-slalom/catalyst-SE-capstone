import { render } from '@testing-library/react'
import Wheel from '../../components/Wheel'

describe('Wheel', () => {
  it('renders without crashing with empty list', () => {
    const { container } = render(<Wheel participants={[]} spinning={false} winner={null} />)
    expect(container.querySelector('canvas')).toBeInTheDocument()
  })

  it('renders without crashing with participants', () => {
    const participants = [{ name: 'Alice', active: true }, { name: 'Bob', active: true }]
    const { container } = render(<Wheel participants={participants} spinning={false} winner={null} />)
    expect(container.querySelector('canvas')).toBeInTheDocument()
  })
})
