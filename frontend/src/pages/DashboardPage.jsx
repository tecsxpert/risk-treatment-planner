import { useEffect, useState } from 'react'
import { api } from '../services/api'
import Card from '../components/Card'
import Loader from '../components/Loader'
import ErrorBanner from '../components/ErrorBanner'
import { AlertTriangle, CheckCircle2, Flame, Users } from 'lucide-react'

function pickNumber(value) {
  const n = Number(value)
  if (Number.isNaN(n)) return null
  return n
}

function extractStats(data) {
  // Flexible parsing for different backend response shapes.
  const total =
    pickNumber(data?.totalRisks) ??
    pickNumber(data?.total) ??
    pickNumber(data?.total_risks)
  const active =
    pickNumber(data?.activeRisks) ??
    pickNumber(data?.active) ??
    pickNumber(data?.active_risks)
  const closed =
    pickNumber(data?.closedRisks) ??
    pickNumber(data?.closed) ??
    pickNumber(data?.closed_risks)

  const highPriority =
    pickNumber(data?.highPriorityRisks) ??
    pickNumber(data?.highRisks) ??
    pickNumber(data?.highPriority) ??
    pickNumber(data?.high_priority_risks)

  return { total, active, closed, highPriority }
}

export default function DashboardPage() {
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [stats, setStats] = useState({
    total: null,
    active: null,
    closed: null,
    highPriority: null,
  })

  useEffect(() => {
    let mounted = true
    async function load() {
      setLoading(true)
      setError('')
      try {
        const res = await api.get('/stats')
        if (!mounted) return
        const payload = res?.data?.data || res?.data?.content || res?.data || {}
        setStats(extractStats(payload))
      } catch (e) {
        if (!mounted) return
        const message =
          e?.response?.data?.message ||
          e?.response?.data?.error ||
          (typeof e?.response?.data === 'string' ? e.response.data : null) ||
          e?.message ||
          'Failed to load dashboard statistics.'
        setError(String(message))
      } finally {
        if (mounted) setLoading(false)
      }
    }
    load()
    return () => {
      mounted = false
    }
  }, [])

  if (loading) return <Loader label="Loading dashboard…" />

  if (error) {
    return <ErrorBanner message={error} title="Could not load dashboard" />
  }

  const showHigh = stats.highPriority !== null && stats.highPriority !== undefined

  return (
    <div className="space-y-5">
      <div>
        <h1 className="text-2xl font-semibold text-[#1B4F8A]">Dashboard</h1>
        <p className="mt-1 text-sm text-gray-600">
          Key metrics for your risk treatment plan.
        </p>
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <div className="flex items-start justify-between gap-3">
            <div className="flex items-center gap-2 text-sm font-semibold text-gray-700">
              <Users
                className="h-4 w-4 text-[#1B4F8A]"
                aria-hidden="true"
              />
              Total risks
            </div>
            <div className="rounded-lg bg-[#1B4F8A]/10 p-2">
              <Users
                className="h-4 w-4 text-[#1B4F8A]"
                aria-hidden="true"
              />
            </div>
          </div>
          <div className="mt-3 text-3xl font-bold tracking-tight text-gray-900">
            {stats.total ?? '—'}
          </div>
        </Card>

        <Card>
          <div className="flex items-start justify-between gap-3">
            <div className="flex items-center gap-2 text-sm font-semibold text-gray-700">
              <AlertTriangle
                className="h-4 w-4 text-[#1B4F8A]"
                aria-hidden="true"
              />
              Active risks
            </div>
            <div className="rounded-lg bg-[#1B4F8A]/10 p-2">
              <AlertTriangle
                className="h-4 w-4 text-[#1B4F8A]"
                aria-hidden="true"
              />
            </div>
          </div>
          <div className="mt-3 text-3xl font-bold tracking-tight text-gray-900">
            {stats.active ?? '—'}
          </div>
        </Card>

        <Card>
          <div className="flex items-start justify-between gap-3">
            <div className="flex items-center gap-2 text-sm font-semibold text-gray-700">
              <CheckCircle2
                className="h-4 w-4 text-[#1B4F8A]"
                aria-hidden="true"
              />
              Closed risks
            </div>
            <div className="rounded-lg bg-[#1B4F8A]/10 p-2">
              <CheckCircle2
                className="h-4 w-4 text-[#1B4F8A]"
                aria-hidden="true"
              />
            </div>
          </div>
          <div className="mt-3 text-3xl font-bold tracking-tight text-gray-900">
            {stats.closed ?? '—'}
          </div>
        </Card>

        {showHigh ? (
          <Card className="border-[#1B4F8A]/20 bg-[#1B4F8A]/5">
            <div className="flex items-start justify-between gap-3">
              <div className="flex items-center gap-2 text-sm font-semibold text-[#1B4F8A]">
                <Flame className="h-4 w-4" aria-hidden="true" />
                High priority
              </div>
              <div className="rounded-lg bg-[#1B4F8A]/15 p-2">
                <Flame className="h-4 w-4 text-[#1B4F8A]" aria-hidden="true" />
              </div>
            </div>
            <div className="mt-3 text-3xl font-bold tracking-tight text-[#1B4F8A]">
              {stats.highPriority ?? '—'}
            </div>
          </Card>
        ) : null}
      </div>
    </div>
  )
}

