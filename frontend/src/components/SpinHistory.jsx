import './SpinHistory.css'

/**
 * @param {{ id: number; pickedName: string; spunAt: string }[]} history
 * @param {() => void} onClose
 */
export default function SpinHistory({ history, onClose }) {
  return (
    <div className="history" data-testid="spin-history">
      <div className="history-header">
        <h2>Spin History</h2>
        <button className="btn-ghost history-close" onClick={onClose}>✕</button>
      </div>
      <ul className="history-items">
        {history.length === 0 && (
          <li className="history-empty">No spins yet. Spin the wheel!</li>
        )}
        {history.map((s, i) => (
          <li key={s.id} className="history-item">
            <span className="history-index">#{i + 1}</span>
            <span className="history-name">{s.pickedName}</span>
            <span className="history-date">
              {new Date(s.spunAt).toLocaleDateString('en-US', {
                month: 'short', day: 'numeric',
              })}
            </span>
          </li>
        ))}
      </ul>
    </div>
  )
}
