export default function Card({ children, className = '' }) {
  return (
    <section
      className={[
        'rounded-xl border border-gray-200 bg-white p-5 shadow-sm transition-shadow duration-200 hover:shadow-md',
        className,
      ].join(' ')}
    >
      {children}
    </section>
  )
}

