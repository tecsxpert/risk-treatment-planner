import { createContext, useContext, useEffect, useMemo, useState } from 'react'

const AuthContext = createContext(null)

function getStoredToken() {
  return localStorage.getItem('token')
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => getStoredToken())

  useEffect(() => {
    const onStorage = (e) => {
      if (e.key !== 'token') return
      setToken(e.newValue)
    }
    window.addEventListener('storage', onStorage)
    return () => window.removeEventListener('storage', onStorage)
  }, [])

  useEffect(() => {
    const onLogout = () => setToken(null)
    window.addEventListener('auth:logout', onLogout)
    return () => window.removeEventListener('auth:logout', onLogout)
  }, [])

  const value = useMemo(
    () => ({
      token,
      isAuthenticated: Boolean(token),
      login: (newToken) => {
        localStorage.setItem('token', newToken)
        setToken(newToken)
      },
      logout: () => {
        localStorage.removeItem('token')
        setToken(null)
        window.dispatchEvent(new Event('auth:logout'))
      },
    }),
    [token],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

