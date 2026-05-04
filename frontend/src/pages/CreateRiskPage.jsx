import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../services/api'
import Loader from '../components/Loader'
import ErrorBanner from '../components/ErrorBanner'
import RiskForm from '../components/RiskForm'

function getId(data) {
  return (
    data?.id ??
    data?._id ??
    data?.riskId ??
    data?.data?.id ??
    data?.data?.data?.id ??
    data?.data?.data?.data?.id
  )
}

export default function CreateRiskPage() {
  const navigate = useNavigate()
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')

  async function handleSubmit(values) {
    setSubmitting(true)
    setError('')
    try {
      const res = await api.post('/create', {
        name: values.name,
        description: values.description || undefined,
        status: values.status,
      })
      const payload = res?.data?.data || res?.data?.content || res?.data || {}
      const id = getId(payload)
      if (id) navigate(`/detail/${id}`)
      else navigate('/list')
    } catch (e) {
      const message =
        e?.response?.data?.message ||
        e?.response?.data?.error ||
        (typeof e?.response?.data === 'string' ? e.response.data : null) ||
        e?.message ||
        'Failed to create risk.'
      setError(String(message))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="text-2xl font-semibold">Create Risk</h1>
        <p className="mt-1 text-sm text-gray-600">
          Add a new risk to your treatment plan.
        </p>
      </div>

      {error ? <ErrorBanner message={error} title="Could not create risk" /> : null}

      <div className="rounded-xl border border-gray-200 bg-white p-5">
        <RiskForm
          initialValues={{ name: '', description: '', status: '' }}
          submitLabel="Create"
          onSubmit={handleSubmit}
          submitting={submitting}
        />
      </div>

      {submitting ? (
        <div className="pt-2">
          <Loader label="Creating risk…" />
        </div>
      ) : null}
    </div>
  )
}

