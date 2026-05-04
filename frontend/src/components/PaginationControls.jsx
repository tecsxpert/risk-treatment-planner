export default function PaginationControls({
  page,
  totalPages,
  onPageChange,
}) {
  if (!totalPages || totalPages <= 1) return null

  const canPrev = page > 1
  const canNext = page < totalPages

  const windowSize = 5
  const half = Math.floor(windowSize / 2)
  let start = Math.max(1, page - half)
  let end = Math.min(totalPages, start + windowSize - 1)
  start = Math.max(1, end - windowSize + 1)

  const pages = []
  for (let p = start; p <= end; p += 1) pages.push(p)

  return (
    <div className="flex flex-wrap items-center justify-between gap-3 pt-4">
      <div className="text-sm text-gray-600">
        Page <span className="font-medium text-gray-900">{page}</span> of{' '}
        <span className="font-medium text-gray-900">{totalPages}</span>
      </div>

      <div className="flex items-center gap-2">
        <button
          type="button"
          onClick={() => onPageChange(page - 1)}
          disabled={!canPrev}
          className="rounded-md border border-gray-200 px-3 py-1 text-sm text-gray-700 hover:bg-gray-50 disabled:opacity-50 focus:outline-none focus:ring-2 focus:ring-[#1B4F8A]/20"
        >
          Prev
        </button>

        {pages.map((p) => (
          <button
            key={p}
            type="button"
            onClick={() => onPageChange(p)}
            className={[
              'rounded-md border px-3 py-1 text-sm',
              p === page
                ? 'border-[#1B4F8A] bg-[#1B4F8A] text-white'
                : 'border-gray-200 bg-white text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-[#1B4F8A]/20',
            ].join(' ')}
          >
            {p}
          </button>
        ))}

        <button
          type="button"
          onClick={() => onPageChange(page + 1)}
          disabled={!canNext}
          className="rounded-md border border-gray-200 px-3 py-1 text-sm text-gray-700 hover:bg-gray-50 disabled:opacity-50 focus:outline-none focus:ring-2 focus:ring-[#1B4F8A]/20"
        >
          Next
        </button>
      </div>
    </div>
  )
}

