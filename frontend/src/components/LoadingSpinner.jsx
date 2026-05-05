export default function LoadingSpinner({ label }) {
  return (
    <div className="flex items-center justify-center gap-3 py-8 text-sm text-gray-600">
      <div
        className="h-5 w-5 animate-spin rounded-full border-2 border-gray-300 border-t-gray-700"
        aria-hidden="true"
      />
      {label ? <span>{label}</span> : null}
    </div>
  )
}

