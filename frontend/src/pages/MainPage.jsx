import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import Wheel from '../components/Wheel'
import ParticipantList from '../components/ParticipantList'
import SpinHistory from '../components/SpinHistory'
import {
  getParticipants, addParticipant, toggleParticipant, deleteParticipant,
  recordSpin, getSpins
} from '../api/api'
import { pickWinner } from '../utils/wheelUtils'
import './MainPage.css'

export default function MainPage() {
  const { logout } = useAuth()
  const navigate = useNavigate()
  const [participants, setParticipants] = useState([])
  const [history, setHistory] = useState([])
  const [spinning, setSpinning] = useState(false)
  const [winner, setWinner] = useState(null)
  const [winnerIndex, setWinnerIndex] = useState(null)
  const [showHistory, setShowHistory] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    getParticipants().then(r => setParticipants(r.data)).catch(() => {})
    getSpins().then(r => setHistory(r.data)).catch(() => {})
  }, [])

  const active = participants.filter(p => p.active)

  async function handleAdd(name) {
    try {
      const res = await addParticipant(name)
      setParticipants(prev => [...prev, res.data])
    } catch { setError('Failed to add participant.') }
  }

  async function handleToggle(id, active) {
    try {
      const res = await toggleParticipant(id, active)
      setParticipants(prev => prev.map(p => p.id === id ? res.data : p))
    } catch { setError('Failed to update participant.') }
  }

  async function handleDelete(id) {
    try {
      await deleteParticipant(id)
      setParticipants(prev => prev.filter(p => p.id !== id))
    } catch { setError('Failed to delete participant.') }
  }

  async function handleSpin() {
    if (active.length < 2 || spinning) return
    setWinner(null)
    const picked = pickWinner(active)
    const idx = active.findIndex(p => p === picked)
    setWinnerIndex(idx)
    setSpinning(true)
    // let the Wheel animation play (~4s), then reveal
    setTimeout(async () => {
      setWinner(picked)
      setSpinning(false)
      try {
        const res = await recordSpin(picked.name)
        setHistory(prev => [res.data, ...prev])
      } catch {}
    }, 4200)
  }

  function handleLogout() {
    logout()
    navigate('/login')
  }

  return (
    <div className="main-page">
      <header className="main-header">
        <div className="main-header-brand">
          <span className="skull">☠</span>
          <span className="brand-text">Wheel of Doom</span>
        </div>
        <div className="main-header-actions">
          <button className="btn-ghost" onClick={() => setShowHistory(h => !h)}>
            {showHistory ? 'Hide History' : 'History'}
          </button>
          <button className="btn-ghost" onClick={handleLogout}>Sign Out</button>
        </div>
      </header>

      {error && <p className="error-msg page-error">{error}</p>}

      <div className="main-body">
        <section className="wheel-section">
          <Wheel
            participants={active}
            spinning={spinning}
            winner={winner}
            winnerIndex={winnerIndex}
          />
          <button
            className="spin-btn"
            onClick={handleSpin}
            disabled={active.length < 2 || spinning}
          >
            {spinning ? 'Spinning…' : active.length < 2 ? 'Add 2+ people' : 'SPIN'}
          </button>
          {winner && !spinning && (
            <div className="winner-banner">
              <span className="winner-label">DOOMED</span>
              <span className="winner-name">{winner.name}</span>
            </div>
          )}
        </section>

        <aside className="side-panel">
          {showHistory
            ? <SpinHistory history={history} onClose={() => setShowHistory(false)} />
            : <ParticipantList
                participants={participants}
                onAdd={handleAdd}
                onToggle={handleToggle}
                onDelete={handleDelete}
              />
          }
        </aside>
      </div>
    </div>
  )
}
