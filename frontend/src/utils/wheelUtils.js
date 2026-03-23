/**
 * Randomly selects a winner from the active participants array.
 * @param {{ name: string }[]} participants
 * @returns {{ name: string }}
 */
export function pickWinner(participants) {
  const index = Math.floor(Math.random() * participants.length)
  return participants[index]
}

/**
 * Calculates the CSS rotation (in degrees) needed to land the wheel on the winning wedge.
 * @param {{ name: string }} winner
 * @param {{ name: string }[]} participants
 * @returns {number} total degrees to rotate
 */
export function calcRotation(winner, participants) {
  // Stub — full implementation will account for wedge size and random full rotations
  return 0
}
