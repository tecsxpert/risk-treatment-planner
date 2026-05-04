import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../services/api'
import { Pencil, Trash2 } from 'lucide-react'
import AiPanel from '../components/AiPanel'
import ConfirmDialog from '../components/ConfirmDialog'
import StatusBadge from '../components/StatusBadge'
import Loader from '../components/Loader'
import ErrorBanner from '../components/ErrorBanner'

function formatValue(v) {
  if (v === null || v === undefined || v === '') return '—'
  if (typeof v === 'string') return v
  if (typeof v === 'number' || typeof v === 'boolean') return String(v)
  if (v instanceof Date) return v.toISOString()
  return JSON.stringify(v, null, 2)
}

export default function RiskDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [risk, setRisk] = useState(null)

  const [deleteOpen, setDeleteOpen] = useState(false)
  const [deleting, setDeleting] = useState(false)

  useEffect(() => {
    let mounted = true
    async function load() {
      setLoading(true)
      setError('')
      try {
        const res = await api.get(`/${id}`)
        if (!mounted) return
        const payload = res?.data?.data || res?.data?.content || res?.data || {}
        setRisk(payload)
      } catch (e) {
        if (!mounted) return
        const message =
          e?.response?.data?.message ||
          e?.response?.data?.error ||
          (typeof e?.response?.data === 'string' ? e.response.data : null) ||
          e?.message ||
          'Failed to load risk details.'
        setError(String(message))
      } finally {
        if (mounted) setLoading(false)
      }
    }
    load()
    return () => {
      mounted = false
    }
  }, [id])

  const status = risk?.status
  const riskId = useMemo(() => id, [id])

  async function handleDelete() {
    setDeleting(true)
    setError('')
    try {
      await api.delete(`/${id}`)
      navigate('/list', { replace: true })
    } catch (e) {
      const message =
        e?.response?.data?.message ||
        e?.response?.data?.error ||
        (typeof e?.response?.data === 'string' ? e.response.data : null) ||
        e?.message ||
        'Failed to delete risk.'
      setError(String(message))
    } finally {
      setDeleting(false)
      setDeleteOpen(false)
    }
  }

  const detailRows = useMemo(() => {
    if (!risk) return []
    return Object.entries(risk).map(([k, v]) => ({ key: k, value: formatValue(v) }))
  }, [risk])

  if (loading) return <Loader label="Loading risk…" />
  if (error) {
    return <ErrorBanner message={error} title="Could not load risk" />
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-[#1B4F8A]">
            {risk?.name ?? 'Risk Detail'}
          </h1>
          <div className="mt-2">
            <StatusBadge status={status} />
          </div>
        </div>

        <div className="flex flex-col sm:flex-row items-center gap-2 w-full sm:w-auto">
          <button
            type="button"
            onClick={() => navigate(`/edit/${id}`)}
            className="w-full sm:w-auto rounded-md border border-[#1B4F8A]/25 bg-white px-4 py-2 text-sm font-semibold text-[#1B4F8A] hover:bg-[#1B4F8A]/5 focus:outline-none focus:ring-2 focus:ring-[#1B4F8A]/20"
          >
            <Pencil className="mr-2 inline h-4 w-4" aria-hidden="true" />
            Edit
          </button>
          <button
            type="button"
            onClick={() => setDeleteOpen(true)}
            className="w-full sm:w-auto rounded-md bg-red-600 px-4 py-2 text-sm font-semibold text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500/30"
          >
            <Trash2 className="mr-2 inline h-4 w-4" aria-hidden="true" />
            Delete
          </button>
        </div>
      </div>

      <div className="rounded-2xl border border-gray-200 bg-white p-5 shadow-sm">
        <h2 className="text-lg font-semibold">Risk Details</h2>
        <div className="mt-4 overflow-x-auto">
          <table className="min-w-full border-collapse text-sm">
            <tbody>
              {detailRows.length ? (
                detailRows.map((row) => (
                  <tr key={row.key} className="border-t border-gray-100">
                    <td className="w-56 px-3 py-3 font-medium text-gray-700">
                      {row.key}
                    </td>
                    <td className="px-3 py-3 text-gray-800">
                      <pre className="whitespace-pre-wrap font-sans">
                        {row.value}
                      </pre>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td className="px-3 py-3 text-gray-600" colSpan={2}>
                    No details available.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      <AiPanel riskId={riskId} risk={risk} />

      <ConfirmDialog
        open={deleteOpen}
        title="Delete this risk?"
        description="This will soft delete the risk. You can export the list later."
        confirmLabel="Delete"
        loading={deleting}
        onClose={() => setDeleteOpen(false)}
        onConfirm={handleDelete}
      />
    </div>
  )
}

