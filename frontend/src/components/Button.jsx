export default function Button({
  children,
  onClick,
  type = 'button',
  disabled = false,
  variant = 'primary',
  className = '',
}) {
  const base =
    'inline-flex items-center justify-center rounded-md px-3 sm:px-4 py-2 min-h-[40px] text-sm font-semibold transition-all duration-200 disabled:cursor-not-allowed disabled:opacity-60'

  const variants = {
    primary:
      'bg-gradient-to-r from-[#1B4F8A] to-[#163f6f] text-white hover:brightness-110 focus:outline-none focus:ring-2 focus:ring-[#1B4F8A]/30',
    secondary:
      'bg-white text-gray-800 ring-1 ring-gray-200 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-[#1B4F8A]/20',
    danger: 'bg-red-600 text-white hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500/30',
    ghost: 'bg-transparent text-gray-700 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-[#1B4F8A]/20',
  }

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={[base, variants[variant] || variants.primary, className].join(' ')}
    >
      {children}
    </button>
  )
}

