import { useEffect, useMemo, useState } from 'react'
import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'
import { api } from '../services/api'
import ErrorBanner from '../components/ErrorBanner'
import Loader from '../components/Loader'
import {
  BarChart3,
  LineChart as LineChartIcon,
  PieChart as PieChartIcon,
} from 'lucide-react'

function extractStatusCounts(data) {
  const raw =
    data?.risksByStatus ||
    data?.byStatus ||
    data?.statusCounts ||
    data?.status_distribution ||
    data?.statusDistribution ||
    data?.distribution

  if (!raw) return []

  if (Array.isArray(raw)) {
    return raw
      .map((r) => ({
        status: r?.status ?? r?.name ?? r?.label ?? r?.key,
        count: Number(r?.count ?? r?.value ?? r?.total ?? 0),
      }))
      .filter((x) => x.status)
  }

  if (typeof raw === 'object') {
    return Object.entries(raw).map(([status, count]) => ({
      status,
      count: Number(count),
    }))
  }

  return []
}

function formatShortDate(value) {
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return String(value)
  return d.toLocaleDateString(undefined, { month: 'short', day: '2-digit' })
}

function extractOverTime(data) {
  const raw = data?.overTime || data?.history || data?.riskOverTime || data?.timeSeries
  if (!raw) return []

  // array of points
  if (Array.isArray(raw)) {
    return raw
      .map((p) => {
        const date = p?.date ?? p?.day ?? p?.timestamp ?? p?.time
        const count = p?.count ?? p?.value ?? p?.total ?? 0
        if (!date) return null
        return { date: String(date), count: Number(count) }
      })
      .filter(Boolean)
      .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
  }

  // object map date -> count
  if (typeof raw === 'object') {
    return Object.entries(raw)
      .map(([date, count]) => ({ date, count: Number(count) }))
      .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
  }

  return []
}

function COLORS() {
  return ['#7c3aed', '#6d28d9', '#2563eb', '#10b981', '#ef4444', '#f59e0b']
}

export default function AnalyticsPage() {
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [stats, setStats] = useState(null)

  useEffect(() => {
    let mounted = true
    async function load() {
      setLoading(true)
      setError('')
      try {
        const res = await api.get('/stats')
        if (!mounted) return
        const payload = res?.data?.data || res?.data?.content || res?.data || {}
        setStats(payload)
      } catch (e) {
        if (!mounted) return
        const message =
          e?.response?.data?.message ||
          e?.response?.data?.error ||
          (typeof e?.response?.data === 'string' ? e.response.data : null) ||
          e?.message ||
          'Failed to load analytics data.'
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

  const statusCounts = useMemo(() => extractStatusCounts(stats || {}), [stats])
  const overTime = useMemo(() => extractOverTime(stats || {}), [stats])

  const pieData = useMemo(() => {
    // For now, reuse status counts as pie distribution.
    return statusCounts.length ? statusCounts : []
  }, [statusCounts])

  if (loading) return <Loader label="Loading analytics…" />
  if (error) {
    return <ErrorBanner message={error} title="Could not load analytics" />
  }

  const colors = COLORS()

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-[#1B4F8A]">Analytics</h1>
        <p className="mt-1 text-sm text-gray-600">
          Status distribution and risk trends.
        </p>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="lg:col-span-2 rounded-2xl border border-gray-200 bg-white p-5 shadow-sm">
          <div className="flex items-center justify-between gap-3">
            <h2 className="flex items-center gap-2 text-lg font-semibold text-[#1B4F8A]">
              <BarChart3 className="h-5 w-5" aria-hidden="true" />
              Risks by Status
            </h2>
            <div className="text-xs text-gray-500">
              {statusCounts.length ? 'Bar chart' : 'No status data'}
            </div>
          </div>
          <div className="mt-4 overflow-x-auto">
            <div className="h-72 min-w-[300px]">
            {statusCounts.length ? (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={statusCounts}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="status" />
                  <YAxis allowDecimals={false} />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="count" name="Risks">
                    {statusCounts.map((_, idx) => (
                      <Cell key={`cell-${idx}`} fill={colors[idx % colors.length]} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="mt-10 text-sm text-gray-600">No data available for the bar chart.</div>
            )}
            </div>
          </div>
        </div>

        <div className="rounded-2xl border border-gray-200 bg-white p-5 shadow-sm">
          <h2 className="flex items-center gap-2 text-lg font-semibold text-[#1B4F8A]">
            <PieChartIcon className="h-5 w-5" aria-hidden="true" />
            Distribution
          </h2>
          <div className="mt-4 overflow-x-auto">
            <div className="h-72 min-w-[300px]">
            {pieData.length ? (
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Tooltip />
                  <Legend />
                  <Pie
                    data={pieData}
                    dataKey="count"
                    nameKey="status"
                    outerRadius={90}
                    label={({ status, percent }) => `${status} ${(percent * 100).toFixed(0)}%`}
                  >
                    {pieData.map((_, idx) => (
                      <Cell key={`pie-${idx}`} fill={colors[idx % colors.length]} />
                    ))}
                  </Pie>
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <div className="mt-10 text-sm text-gray-600">No distribution data available.</div>
            )}
            </div>
          </div>
        </div>
      </div>

      <div className="rounded-2xl border border-gray-200 bg-white p-5 shadow-sm">
        <div className="flex items-center justify-between gap-3">
          <h2 className="flex items-center gap-2 text-lg font-semibold text-[#1B4F8A]">
            <LineChartIcon className="h-5 w-5" aria-hidden="true" />
            Over Time
          </h2>
          <div className="text-xs text-gray-500">
            {overTime.length ? 'Line chart' : 'No time-series data'}
          </div>
        </div>
        <div className="mt-4 overflow-x-auto">
          <div className="h-72 min-w-[300px]">
          {overTime.length ? (
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={overTime.map((p) => ({ ...p, label: formatShortDate(p.date) }))}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="label" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="count" name="Risks" stroke="#7c3aed" strokeWidth={2} />
              </LineChart>
            </ResponsiveContainer>
          ) : (
            <div className="mt-10 text-sm text-gray-600">No time-series data available from the backend.</div>
          )}
          </div>
        </div>
      </div>
    </div>
  )
}

