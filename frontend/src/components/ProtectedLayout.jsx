import RequireAuth from './RequireAuth'
import MainLayout from './MainLayout'

export default function ProtectedLayout() {
  return (
    <RequireAuth>
      <MainLayout />
    </RequireAuth>
  )
}

