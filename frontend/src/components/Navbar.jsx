import { NavLink, useNavigate } from 'react-router-dom'
import { Activity, BarChart3, LayoutDashboard, List, LogOut, Plus } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

const linkClasses = ({ isActive }) =>
  [
    'flex flex-col items-center justify-center gap-1 px-3 py-1.5 rounded-md transition duration-200 hover:scale-105 min-w-[72px]',
    isActive
      ? 'bg-white/20 font-semibold text-white shadow-sm'
      : 'text-blue-50 hover:bg-white/10 hover:text-yellow-300',
  ].join(' ')

export default function Navbar() {
  const { logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/', { replace: true })
  }

  return (
    <header className="sticky top-0 z-50 flex w-full flex-wrap items-center justify-between gap-4 bg-gradient-to-r from-[#1B4F8A] via-blue-700 to-blue-500 px-6 py-3 shadow-lg backdrop-blur-md md:min-h-16 md:py-2">
      
      {/* LEFT SECTION (LOGO + TITLE) */}
      <div className="flex items-center gap-2 group cursor-pointer transition-transform duration-200 hover:scale-105">
        <div className="flex items-center justify-center rounded-lg bg-white/20 p-1.5 backdrop-blur-sm">
          <Activity className="h-5 w-5 text-white" aria-hidden="true" />
        </div>
        <div className="flex flex-col leading-none">
          <span className="text-xl font-bold tracking-wide text-white drop-shadow-sm hidden sm:block">
            Risk Planner
          </span>
          <span className="mt-0.5 text-xs font-medium text-blue-100 hidden sm:block">
            Enterprise Dashboard
          </span>
        </div>
      </div>

      {/* NAV LINKS (CENTER OR RIGHT) */}
      <nav className="flex flex-1 flex-wrap items-center justify-center gap-2 md:gap-4">
        <NavLink to="/dashboard" className={linkClasses}>
          <LayoutDashboard className="h-5 w-5" aria-hidden="true" />
          <span className="text-[10px] md:text-xs">Dashboard</span>
        </NavLink>
        <NavLink to="/list" className={linkClasses}>
          <List className="h-5 w-5" aria-hidden="true" />
          <span className="text-[10px] md:text-xs">List</span>
        </NavLink>
        <NavLink to="/create" className={linkClasses}>
          <Plus className="h-5 w-5" aria-hidden="true" />
          <span className="text-[10px] md:text-xs">Create</span>
        </NavLink>
        <NavLink to="/analytics" className={linkClasses}>
          <BarChart3 className="h-5 w-5" aria-hidden="true" />
          <span className="text-[10px] md:text-xs">Analytics</span>
        </NavLink>
      </nav>

      {/* RIGHT SECTION (AVATAR + LOGOUT) */}
      <div className="flex items-center gap-4">
        {/* User Avatar */}
        <div className="flex h-8 w-8 items-center justify-center rounded-full border border-white/30 bg-blue-600/50 text-sm font-bold text-white shadow-sm transition-transform duration-200 hover:scale-105 cursor-pointer hidden sm:flex">
          AD
        </div>

        {/* Logout Button */}
        <button
          type="button"
          onClick={handleLogout}
          className="flex items-center gap-1.5 rounded-md bg-red-500 px-3 py-1.5 text-sm font-medium text-white shadow-md transition-all duration-200 hover:scale-105 hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-400"
        >
          <LogOut className="h-4 w-4" aria-hidden="true" />
          <span>Logout</span>
        </button>
      </div>
    </header>
  )
}

