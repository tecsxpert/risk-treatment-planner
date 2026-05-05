export default function SearchAndFilters({
  search,
  onSearchChange,
  status,
  onStatusChange,
  startDate,
  onStartDateChange,
  endDate,
  onEndDateChange,
  statuses = ['All', 'Active', 'Closed', 'Open', 'In Progress'],
}) {
  return (
    <div className="rounded-xl border border-gray-200 bg-white p-4">
      <div className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div className="flex-1">
          <label className="mb-1 block text-sm font-medium text-gray-700">
            Search
          </label>
          <input
            type="text"
            value={search}
            onChange={(e) => onSearchChange(e.target.value)}
            placeholder="Search risks by name or description"
            className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm outline-none focus:border-[#1B4F8A] focus:ring-2 focus:ring-[#1B4F8A]/20"
          />
        </div>

        <div className="w-full md:w-48">
          <label className="mb-1 block text-sm font-medium text-gray-700">
            Status
          </label>
          <select
            value={status}
            onChange={(e) => onStatusChange(e.target.value)}
            className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm outline-none focus:border-[#1B4F8A] focus:ring-2 focus:ring-[#1B4F8A]/20"
          >
            {statuses.map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </select>
        </div>

        <div className="flex w-full flex-col gap-3 md:flex-row md:items-end">
          <div className="w-full md:w-44">
            <label className="mb-1 block text-sm font-medium text-gray-700">
              From
            </label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => onStartDateChange(e.target.value)}
              className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm outline-none focus:border-[#1B4F8A] focus:ring-2 focus:ring-[#1B4F8A]/20"
            />
          </div>
          <div className="w-full md:w-44">
            <label className="mb-1 block text-sm font-medium text-gray-700">
              To
            </label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => onEndDateChange(e.target.value)}
              className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm outline-none focus:border-[#1B4F8A] focus:ring-2 focus:ring-[#1B4F8A]/20"
            />
          </div>
        </div>
      </div>
    </div>
  )
}

