import { useRef, useEffect, useState } from 'react'

const COLORS = [
  '#f59e0b', '#ef4444', '#3b82f6', '#10b981',
  '#8b5cf6', '#f97316', '#06b6d4', '#ec4899',
]

/**
 * @param {{ name: string; active: boolean }[]} participants
 * @param {boolean} spinning
 * @param {{ name: string } | null} winner
 */
export default function Wheel({ participants, spinning, winner, winnerIndex }) {
  const canvasRef = useRef(null)
  const rotationRef = useRef(0)
  const animRef = useRef(null)
  const [displayRotation, setDisplayRotation] = useState(0)

  const count = participants.length

  // Draw wheel on canvas whenever participants or rotation changes
  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return
    const ctx = canvas.getContext('2d')
    const size = canvas.width
    const cx = size / 2
    const cy = size / 2
    const r = cx - 8

    ctx.clearRect(0, 0, size, size)

    if (count === 0) {
      // Empty state
      ctx.beginPath()
      ctx.arc(cx, cy, r, 0, Math.PI * 2)
      ctx.fillStyle = '#1f1d1c'
      ctx.fill()
      ctx.strokeStyle = '#2e2b29'
      ctx.lineWidth = 2
      ctx.stroke()
      ctx.fillStyle = '#8a8278'
      ctx.font = '500 14px "DM Sans", sans-serif'
      ctx.textAlign = 'center'
      ctx.textBaseline = 'middle'
      ctx.fillText('Add people to spin', cx, cy)
      return
    }

    const slice = (Math.PI * 2) / count
    const rot = (displayRotation * Math.PI) / 180

    for (let i = 0; i < count; i++) {
      const start = rot + i * slice
      const end = start + slice
      // Wedge
      ctx.beginPath()
      ctx.moveTo(cx, cy)
      ctx.arc(cx, cy, r, start, end)
      ctx.closePath()
      ctx.fillStyle = COLORS[i % COLORS.length]
      ctx.fill()
      ctx.strokeStyle = '#0e0d0d'
      ctx.lineWidth = 2
      ctx.stroke()

      // Label
      ctx.save()
      ctx.translate(cx, cy)
      ctx.rotate(start + slice / 2)
      ctx.textAlign = 'right'
      ctx.fillStyle = '#fff'
      ctx.font = 'bold 13px "DM Sans", sans-serif'
      ctx.shadowColor = '#00000080'
      ctx.shadowBlur = 4
      const label = participants[i].name.length > 12
        ? participants[i].name.slice(0, 11) + '…'
        : participants[i].name
      ctx.fillText(label, r - 12, 4)
      ctx.restore()
    }

    // Center hub
    ctx.beginPath()
    ctx.arc(cx, cy, 18, 0, Math.PI * 2)
    ctx.fillStyle = '#0e0d0d'
    ctx.fill()
    ctx.beginPath()
    ctx.arc(cx, cy, 10, 0, Math.PI * 2)
    ctx.fillStyle = '#f59e0b'
    ctx.fill()
  }, [participants, displayRotation, count])

  // Spin animation — lands deterministically on the winner's wedge
  useEffect(() => {
    if (!spinning || winnerIndex == null || count === 0) return
    const slice = 360 / count
    // Center of winner's wedge must align with the pointer (top = -90°)
    const targetBase = (((-90 - (winnerIndex + 0.5) * slice) % 360) + 360) % 360
    const current = rotationRef.current
    const extraSpins = (Math.ceil((current + 1440 - targetBase) / 360)) * 360
    const target = targetBase + extraSpins
    const start = current
    const duration = 4000
    const startTime = performance.now()

    function easeOut(t) {
      return 1 - Math.pow(1 - t, 4)
    }

    function frame(now) {
      const elapsed = now - startTime
      const t = Math.min(elapsed / duration, 1)
      const current = start + (target - start) * easeOut(t)
      rotationRef.current = current
      setDisplayRotation(current)
      if (t < 1) animRef.current = requestAnimationFrame(frame)
    }

    animRef.current = requestAnimationFrame(frame)
    return () => cancelAnimationFrame(animRef.current)
  }, [spinning, winnerIndex]) // eslint-disable-line

  const size = 340

  return (
    <div style={{ position: 'relative', width: size, height: size }}>
      {/* Pointer */}
      <div style={{
        position: 'absolute',
        top: -10,
        left: '50%',
        transform: 'translateX(-50%)',
        width: 0,
        height: 0,
        borderLeft: '10px solid transparent',
        borderRight: '10px solid transparent',
        borderTop: '28px solid #f59e0b',
        zIndex: 2,
        filter: 'drop-shadow(0 2px 4px #00000060)',
      }} />
      <canvas
        ref={canvasRef}
        width={size}
        height={size}
        style={{
          borderRadius: '50%',
          boxShadow: spinning
            ? '0 0 60px #f59e0b50, 0 0 120px #f59e0b20'
            : '0 0 30px #00000080',
          transition: 'box-shadow 0.3s ease',
        }}
      />
    </div>
  )
}
