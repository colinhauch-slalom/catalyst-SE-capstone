import '@testing-library/jest-dom'

// Mock canvas for jsdom
HTMLCanvasElement.prototype.getContext = () => ({
  clearRect: () => {},
  beginPath: () => {},
  moveTo: () => {},
  arc: () => {},
  closePath: () => {},
  fill: () => {},
  stroke: () => {},
  fillText: () => {},
  save: () => {},
  restore: () => {},
  translate: () => {},
  rotate: () => {},
  set fillStyle(_) {},
  set strokeStyle(_) {},
  set lineWidth(_) {},
  set font(_) {},
  set textAlign(_) {},
  set textBaseline(_) {},
  set shadowColor(_) {},
  set shadowBlur(_) {},
})
