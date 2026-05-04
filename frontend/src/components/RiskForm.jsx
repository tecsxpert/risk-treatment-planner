import { useEffect, useMemo, useState } from 'react'
import { Save } from 'lucide-react'

export default function RiskForm({
  initialValues,
  submitLabel,
  onSubmit,
  submitting = false,
}) {
  const defaultValues = useMemo(
    () => ({
      name: '',
      description: '',
      status: '',
      ...initialValues,
    }),
    [initialValues],
  )

  const [values, setValues] = useState(defaultValues)
  const [errors, setErrors] = useState({})

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setValues(defaultValues)
  }, [defaultValues])

  function validate(nextValues) {
    const nextErrors = {}
    if (!nextValues.name || !String(nextValues.name).trim()) {
      nextErrors.name = 'Name is required.'
    }
    if (!nextValues.status || !String(nextValues.status).trim()) {
      nextErrors.status = 'Status is required.'
    }
    // description optional
    return nextErrors
  }

  const handleChange = (key, val) => {
    const next = { ...values, [key]: val }
    setValues(next)
    // Keep validation responsive without being too chatty.
    setErrors((prev) => ({ ...prev, ...validate(next) }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    const nextErrors = validate(values)
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length) return
    onSubmit(values)
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-3 sm:gap-5">
      <div>
        <label className="mb-1 block text-sm font-medium text-gray-700">
          Name
        </label>
        <input
          value={values.name}
          onChange={(e) => handleChange('name', e.target.value)}
          className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm outline-none focus:border-[#1B4F8A] focus:ring-2 focus:ring-[#1B4F8A]/20"
          placeholder="e.g., Data breach risk"
        />
        {errors.name ? (
          <div className="mt-1 text-xs text-red-600">{errors.name}</div>
        ) : null}
      </div>

      <div>
        <label className="mb-1 block text-sm font-medium text-gray-700">
          Description (optional)
        </label>
        <textarea
          value={values.description}
          onChange={(e) => handleChange('description', e.target.value)}
          className="min-h-[110px] w-full resize-y rounded-md border border-gray-200 bg-white px-3 py-2 text-sm outline-none focus:border-[#1B4F8A] focus:ring-2 focus:ring-[#1B4F8A]/20"
          placeholder="Add context for the risk treatment plan"
        />
      </div>

      <div>
        <label className="mb-1 block text-sm font-medium text-gray-700">
          Status
        </label>
        <select
          value={values.status}
          onChange={(e) => handleChange('status', e.target.value)}
          className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm outline-none focus:border-[#1B4F8A] focus:ring-2 focus:ring-[#1B4F8A]/20"
        >
          <option value="">Select status</option>
          <option value="Active">Active</option>
          <option value="Open">Open</option>
          <option value="In Progress">In Progress</option>
          <option value="Closed">Closed</option>
        </select>
        {errors.status ? (
          <div className="mt-1 text-xs text-red-600">{errors.status}</div>
        ) : null}
      </div>

      <div className="flex flex-col sm:flex-row justify-end gap-2 mt-2">
        <button
          type="submit"
          disabled={submitting}
          className="w-full sm:w-auto rounded-md bg-[#1B4F8A] px-4 py-2 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-[#163f6f] disabled:opacity-60"
        >
          <Save className="mr-2 h-4 w-4" aria-hidden="true" />
          {submitting ? 'Saving…' : submitLabel}
        </button>
      </div>
    </form>
  )
}

