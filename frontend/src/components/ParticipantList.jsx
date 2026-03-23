import { useState } from 'react'
import './ParticipantList.css'

/**
 * @param {{ id: number; name: string; active: boolean }[]} participants
 * @param {(name: string) => void} onAdd
 * @param {(id: number, active: boolean) => void} onToggle
 * @param {(id: number) => void} onDelete
 */
export default function ParticipantList({ participants, onAdd, onToggle, onDelete }) {
  const [input, setInput] = useState('')

  function handleAdd(e) {
    e.preventDefault()
    const name = input.trim()
    if (!name) return
    onAdd(name)
    setInput('')
  }

  return (
    <div className="plist" data-testid="participant-list">
      <div className="plist-header">
        <h2>Participants</h2>
        <span className="plist-count">{participants.filter(p => p.active).length} active</span>
      </div>

      <form className="plist-add" onSubmit={handleAdd}>
        <input
          type="text"
          placeholder="Add a name…"
          value={input}
          onChange={e => setInput(e.target.value)}
          maxLength={40}
        />
        <button type="submit" className="plist-add-btn" disabled={!input.trim()}>+</button>
      </form>

      <ul className="plist-items">
        {participants.length === 0 && (
          <li className="plist-empty">No participants yet. Add someone above.</li>
        )}
        {participants.map(p => (
          <li key={p.id} className={`plist-item ${p.active ? '' : 'inactive'}`}>
            <button
              className={`plist-toggle ${p.active ? 'on' : 'off'}`}
              onClick={() => onToggle(p.id, !p.active)}
              title={p.active ? 'Deactivate' : 'Activate'}
            >
              {p.active ? '●' : '○'}
            </button>
            <span className="plist-name">{p.name}</span>
            <button
              className="btn-danger plist-delete"
              onClick={() => onDelete(p.id)}
              title="Remove"
            >
              ✕
            </button>
          </li>
        ))}
      </ul>
    </div>
  )
}
