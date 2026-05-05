function badgeTone(status) {
  const s = String(status || '').toLowerCase()
  if (s.includes('active') || s.includes('open'))
    return 'bg-emerald-100 text-emerald-800 border border-emerald-200'
  if (s.includes('in progress') || s.includes('progress') || s.includes('pending'))
    return 'bg-yellow-100 text-yellow-800 border border-yellow-200'
  if (s.includes('closed') || s.includes('resolved') || s.includes('close'))
    return 'bg-red-100 text-red-800 border border-red-200'
  return 'bg-gray-100 text-gray-800 border border-gray-200'
}

export default function StatusBadge({ status }) {
  return (
    <span
      className={[
        'inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold',
        badgeTone(status),
      ].join(' ')}
    >
      {status || 'Unknown'}
    </span>
  )
}

