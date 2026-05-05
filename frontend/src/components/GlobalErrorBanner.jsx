import { useEffect, useState } from 'react'

export default function GlobalErrorBanner() {
  const [message, setMessage] = useState('')
  const [visible, setVisible] = useState(false)

  useEffect(() => {
    const handler = (e) => {
      const next = e?.detail?.message
      if (!next) return
      setMessage(next)
      setVisible(true)
      window.clearTimeout(window.__apiErrorTimer)
      window.__apiErrorTimer = window.setTimeout(() => setVisible(false), 6000)
    }

    window.addEventListener('api:error', handler)
    return () => window.removeEventListener('api:error', handler)
  }, [])

  if (!visible) return null

  return (
    <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">
      <div className="flex items-start justify-between gap-3">
        <div className="flex-1">
          <div className="font-semibold">Request failed</div>
          <div className="mt-1">{message}</div>
        </div>
        <button
          type="button"
          className="rounded-md px-2 py-1 text-red-700 hover:bg-red-100"
          onClick={() => setVisible(false)}
        >
          Dismiss
        </button>
      </div>
    </div>
  )
}

