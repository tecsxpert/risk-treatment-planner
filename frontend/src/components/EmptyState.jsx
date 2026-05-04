export default function EmptyState({
  title = 'No results',
  description = 'Try adjusting your filters.',
}) {
  return (
    <div className="rounded-xl border border-gray-200 bg-white p-8 text-center">
      <div
        className="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-full bg-gray-50 ring-1 ring-gray-200"
        aria-hidden="true"
      >
        <svg
          width="22"
          height="22"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M12 5v14M5 12h14"
            stroke="#6B7280"
            strokeWidth="2"
            strokeLinecap="round"
          />
        </svg>
      </div>
      <div className="text-sm font-semibold text-gray-900">{title}</div>
      <div className="mt-1 text-sm text-gray-600">{description}</div>
    </div>
  )
}

