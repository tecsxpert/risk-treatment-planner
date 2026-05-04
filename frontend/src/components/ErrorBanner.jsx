export default function ErrorBanner({ message, title = 'Something went wrong' }) {
  if (!message) return null

  return (
    <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">
      <div className="font-semibold">{title}</div>
      <div className="mt-1">{message}</div>
    </div>
  )
}

