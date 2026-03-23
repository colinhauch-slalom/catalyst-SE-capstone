import { describe, it, expect } from 'vitest'
import { pickWinner } from '../../utils/wheelUtils'

describe('pickWinner', () => {
  it('returns an element from the participants array', () => {
    const participants = [{ name: 'Alice' }, { name: 'Bob' }, { name: 'Charlie' }]
    const winner = pickWinner(participants)
    expect(participants).toContain(winner)
  })

  it('works with a single participant', () => {
    const participants = [{ name: 'Solo' }]
    expect(pickWinner(participants)).toEqual({ name: 'Solo' })
  })
})
