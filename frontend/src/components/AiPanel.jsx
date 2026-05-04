import { useMemo, useState } from 'react'
import { api } from '../services/api'
import LoadingSpinner from './LoadingSpinner'
import { Brain, Circle, Sparkles } from 'lucide-react'

function extractErrorMessage(error) {
  const data = error?.response?.data
  return (
    error?.response?.data?.message ||
    error?.response?.data?.error ||
    (typeof data === 'string' ? data : null) ||
    error?.message ||
    'AI request failed. Please try again.'
  )
}

export default function AiPanel({ riskId, risk }) {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [description, setDescription] = useState('')
  const [recommendations, setRecommendations] = useState([])
  const [askedOnce, setAskedOnce] = useState(false)

  const payload = useMemo(() => ({ id: riskId, risk }), [riskId, risk])

  async function askAi() {
    setLoading(true)
    setError('')
    try {
      setAskedOnce(true)
      const [describeRes, recommendRes] = await Promise.all([
        api.post('/describe', payload),
        api.post('/recommend', payload),
      ])

      const descData = describeRes?.data?.data || describeRes?.data?.content || describeRes?.data || {}
      const nextDescription =
        descData?.description || descData?.text || descData?.detail || descData
      setDescription(nextDescription ? String(nextDescription) : '')

      const recData = recommendRes?.data?.data || recommendRes?.data?.content || recommendRes?.data || {}
      const rawRecs =
        recData?.recommendations ||
        recData?.items ||
        recData?.data ||
        (Array.isArray(recData) ? recData : null) ||
        (typeof recData === 'string' ? [recData] : null)

      if (Array.isArray(rawRecs)) {
        setRecommendations(
          rawRecs.map((r) => {
            if (typeof r === 'string') return r
            const maybe =
              r?.text ?? r?.recommendation ?? r?.value ?? r?.recommend ?? null
            if (maybe) return String(maybe)
            try {
              return JSON.stringify(r)
            } catch {
              return String(r)
            }
          }),
        )
      } else {
        setRecommendations([])
      }
    } catch (e) {
      setError(extractErrorMessage(e))
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="rounded-2xl border border-gray-200 bg-white p-5 shadow-sm">
      <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
        <div>
          <h2 className="flex items-center gap-2 text-lg font-semibold text-[#1B4F8A]">
            <Brain className="h-5 w-5" aria-hidden="true" />
            AI Panel
          </h2>
          <div className="text-sm text-gray-600">
            Get description and recommendations for this risk.
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={askAi}
            disabled={loading}
            className="rounded-md bg-[#1B4F8A] px-4 py-2 text-sm font-semibold text-white hover:bg-[#163f6f] focus:outline-none focus:ring-2 focus:ring-[#1B4F8A]/30 disabled:opacity-60"
          >
            <Sparkles className="mr-2 inline h-4 w-4" aria-hidden="true" />
            {loading ? 'Asking AI…' : 'Ask AI'}
          </button>
          {askedOnce && !loading && error ? (
            <button
              type="button"
              onClick={askAi}
              className="rounded-md border border-gray-200 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-[#1B4F8A]/20"
            >
              Retry
            </button>
          ) : null}
        </div>
      </div>

      {loading ? <LoadingSpinner label="Analyzing…" /> : null}

      {error ? (
        <div className="mt-4 rounded-xl border border-red-200 bg-red-50 p-3 text-sm text-red-800">
          {error}
        </div>
      ) : null}

      {!loading && !error ? (
        <div className="mt-5 space-y-5">
          <div>
            <div className="text-sm font-medium text-gray-800">Description</div>
            <div className="mt-2 whitespace-pre-wrap rounded-xl bg-gray-50 p-3 text-sm text-gray-800 ring-1 ring-gray-100">
              {description || '—'}
            </div>
          </div>

          <div>
            <div className="text-sm font-medium text-gray-800">Recommendations</div>
            {recommendations.length ? (
              <ul className="mt-2 space-y-2">
                {recommendations.map((r, idx) => (
                  <li
                    key={`${idx}-${r.slice(0, 12)}`}
                    className="rounded-xl border border-gray-200 bg-white p-3 text-sm text-gray-800 shadow-sm"
                  >
                    <div className="flex items-start gap-2">
                      <Circle className="mt-1 h-4 w-4 text-[#1B4F8A]" aria-hidden="true" />
                      <div className="flex-1">{r}</div>
                    </div>
                  </li>
                ))}
              </ul>
            ) : (
              <div className="mt-2 rounded-xl bg-gray-50 p-3 text-sm text-gray-600 ring-1 ring-gray-100">
                No recommendations yet.
              </div>
            )}
          </div>
        </div>
      ) : null}
    </section>
  )
}

