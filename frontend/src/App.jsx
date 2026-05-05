import { Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import ProtectedLayout from './components/ProtectedLayout'
import ErrorToast from './components/ErrorToast'

import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import RiskListPage from './pages/RiskListPage'
import CreateRiskPage from './pages/CreateRiskPage'
import EditRiskPage from './pages/EditRiskPage'
import RiskDetailPage from './pages/RiskDetailPage'
import AnalyticsPage from './pages/AnalyticsPage'

export default function App() {
  return (
    <AuthProvider>
      <ErrorToast />
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route element={<ProtectedLayout />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/list" element={<RiskListPage />} />
          <Route path="/create" element={<CreateRiskPage />} />
          <Route path="/edit/:id" element={<EditRiskPage />} />
          <Route path="/detail/:id" element={<RiskDetailPage />} />
          <Route path="/analytics" element={<AnalyticsPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AuthProvider>
  )
}
