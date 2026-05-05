import { memo } from 'react'
import { Eye, Pencil, Trash2 } from 'lucide-react'

function formatDate(value) {
  if (!value) return '—'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return String(value)
  return d.toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
  })
}

const RiskTable = memo(function RiskTable({ risks, onView, onEdit, onDelete }) {
  return (
    <div className="overflow-x-auto rounded-2xl border border-gray-200 bg-white shadow-sm">
      <table className="min-w-full border-collapse text-xs sm:text-sm">
        <thead className="bg-[#1B4F8A]/5">
          <tr className="text-left text-gray-600">
            <th className="p-2 sm:px-4 sm:py-3 font-medium">Name</th>
            <th className="p-2 sm:px-4 sm:py-3 font-medium">Status</th>
            <th className="p-2 sm:px-4 sm:py-3 font-medium">Date</th>
            <th className="p-2 sm:px-4 sm:py-3 font-medium">Actions</th>
          </tr>
        </thead>
        <tbody>
          {risks.map((r) => {
            const dateValue = r.date || r.createdAt || r.created_at || r.createdDate
            return (
              <tr
                key={r.id ?? r._id ?? r.riskId}
                className="border-t border-gray-100 odd:bg-white even:bg-gray-50 hover:bg-[#1B4F8A]/5 transition-colors"
              >
                <td className="p-2 sm:px-4 sm:py-3 font-medium text-gray-900">
                  {r.name ?? 'Untitled'}
                </td>
                <td className="p-2 sm:px-4 sm:py-3 text-gray-700">{r.status ?? '—'}</td>
                <td className="p-2 sm:px-4 sm:py-3 text-gray-600">{formatDate(dateValue)}</td>
                <td className="p-2 sm:px-4 sm:py-3">
                  <div className="flex flex-wrap items-center gap-2">
                    <button
                      type="button"
                      onClick={() => onView?.(r)}
                      className="flex items-center justify-center rounded-md border border-[#1B4F8A]/30 bg-white px-3 py-1 text-xs font-semibold text-[#1B4F8A] hover:bg-[#1B4F8A]/10 focus:outline-none focus:ring-2 focus:ring-[#1B4F8A]/20"
                    >
                      <Eye className="mr-1.5 h-3.5 w-3.5" aria-hidden="true" />
                      View
                    </button>
                    <button
                      type="button"
                      onClick={() => onEdit?.(r)}
                      className="flex items-center justify-center rounded-md border border-[#1B4F8A]/30 bg-white px-3 py-1 text-xs font-semibold text-[#1B4F8A] hover:bg-[#1B4F8A]/10 focus:outline-none focus:ring-2 focus:ring-[#1B4F8A]/20"
                    >
                      <Pencil className="mr-1.5 h-3.5 w-3.5" aria-hidden="true" />
                      Edit
                    </button>
                    <button
                      type="button"
                      onClick={() => onDelete?.(r)}
                      className="flex items-center justify-center rounded-md bg-red-600 px-3 py-1 text-xs font-semibold text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500/30"
                    >
                      <Trash2 className="mr-1.5 h-3.5 w-3.5" aria-hidden="true" />
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
})

export default RiskTable

