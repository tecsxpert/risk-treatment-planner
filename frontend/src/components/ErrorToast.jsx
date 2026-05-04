import { useEffect, useState } from 'react'
import ErrorBanner from './ErrorBanner'

export default function ErrorToast() {
  const [message, setMessage] = useState('')
  const [visible, setVisible] = useState(false)

  useEffect(() => {
    const handler = (e) => {
      const next = e?.detail?.message
      if (!next) return
      setMessage(String(next))
      setVisible(true)
    }

    window.addEventListener('api:error', handler)
    return () => window.removeEventListener('api:error', handler)
  }, [])

  useEffect(() => {
    if (!visible) return
    window.clearTimeout(window.__apiToastTimer)
    window.__apiToastTimer = window.setTimeout(() => setVisible(false), 6000)
  }, [visible])

  if (!visible || !message) return null

  return (
    <div className="mb-4">
      <div className="flex items-start justify-between gap-3">
        <ErrorBanner message={message} title="Request failed" />
        <button
          type="button"
          onClick={() => setVisible(false)}
          className="mt-1 rounded-md px-2 py-1 text-red-700 hover:bg-red-100"
        >
          Dismiss
        </button>
      </div>
    </div>
  )
}

