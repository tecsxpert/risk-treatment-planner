import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../services/api'
import Loader from '../components/Loader'
import ErrorBanner from '../components/ErrorBanner'
import RiskForm from '../components/RiskForm'

export default function EditRiskPage() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [initial, setInitial] = useState({ name: '', description: '', status: '' })

  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    let mounted = true
    async function load() {
      setLoading(true)
      setError('')
      try {
        const res = await api.get(`/${id}`)
        if (!mounted) return
        const payload = res?.data?.data || res?.data?.content || res?.data || {}
        setInitial({
          name: payload.name ?? '',
          description: payload.description ?? '',
          status: payload.status ?? '',
        })
      } catch (e) {
        if (!mounted) return
        const message =
          e?.response?.data?.message ||
          e?.response?.data?.error ||
          (typeof e?.response?.data === 'string' ? e.response.data : null) ||
          e?.message ||
          'Failed to load risk for editing.'
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

  async function handleSubmit(values) {
    setSubmitting(true)
    setError('')
    try {
      await api.put(`/${id}`, {
        name: values.name,
        description: values.description || undefined,
        status: values.status,
      })
      navigate(`/detail/${id}`)
    } catch (e) {
      const message =
        e?.response?.data?.message ||
        e?.response?.data?.error ||
        (typeof e?.response?.data === 'string' ? e.response.data : null) ||
        e?.message ||
        'Failed to update risk.'
      setError(String(message))
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) return <Loader label="Loading risk…" />
  if (error) return <ErrorBanner message={error} title="Could not load risk" />

  return (
    <div className="space-y-5">
      <div>
        <h1 className="text-2xl font-semibold">Edit Risk</h1>
        <p className="mt-1 text-sm text-gray-600">
          Update details and status.
        </p>
      </div>

      <div className="rounded-xl border border-gray-200 bg-white p-5">
        <RiskForm
          initialValues={initial}
          submitLabel="Save Changes"
          onSubmit={handleSubmit}
          submitting={submitting}
        />
      </div>

      {submitting ? (
        <div className="pt-2">
          <Loader label="Saving changes…" />
        </div>
      ) : null}
    </div>
  )
}

