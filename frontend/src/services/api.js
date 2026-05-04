import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const MOCK = {
  token: 'mock-jwt-token',
}

let mockRisks = [
  { id: 'r1', name: 'Data breach risk', description: 'Potential exposure of customer PII via misconfigured systems.', status: 'Active', priority: 'High', date: '2026-04-10', createdAt: '2026-04-10T10:00:00.000Z' },
  { id: 'r2', name: 'Unauthorized access', description: 'Weak IAM controls could allow privilege escalation.', status: 'In Progress', priority: 'Medium', date: '2026-03-18', createdAt: '2026-03-18T09:30:00.000Z' },
  { id: 'r3', name: 'Service outage', description: 'Third-party dependency failures may impact availability.', status: 'Open', priority: 'Low', date: '2026-02-08', createdAt: '2026-02-08T12:15:00.000Z' },
  { id: 'r4', name: 'Compliance gaps', description: 'Missing audit evidence for recurring processes.', status: 'Closed', priority: 'High', date: '2026-01-20', createdAt: '2026-01-20T08:40:00.000Z' },
  { id: 'r5', name: 'Invoice processing errors', description: 'Incorrect billing rules leading to financial inconsistencies.', status: 'Active', priority: 'High', date: '2025-12-11', createdAt: '2025-12-11T11:20:00.000Z' },
  { id: 'r6', name: 'Phishing attack vulnerability', description: 'Employees susceptible to targeted email phishing campaigns.', status: 'Active', priority: 'High', date: '2026-04-28', createdAt: '2026-04-28T14:00:00.000Z' },
  { id: 'r7', name: 'Unpatched server vulnerabilities', description: 'Legacy systems missing critical security patches.', status: 'In Progress', priority: 'High', date: '2026-04-15', createdAt: '2026-04-15T09:15:00.000Z' },
  { id: 'r8', name: 'Vendor lock-in', description: 'Heavy reliance on a single cloud provider for core infrastructure.', status: 'Open', priority: 'Medium', date: '2026-03-05', createdAt: '2026-03-05T11:45:00.000Z' },
  { id: 'r9', name: 'Loss of key personnel', description: 'Risk of critical knowledge loss if lead engineers depart.', status: 'In Progress', priority: 'Medium', date: '2026-02-28', createdAt: '2026-02-28T16:20:00.000Z' },
  { id: 'r10', name: 'Inadequate disaster recovery', description: 'Current backups have not been tested for full restoration.', status: 'Active', priority: 'High', date: '2026-02-14', createdAt: '2026-02-14T08:00:00.000Z' },
  { id: 'r11', name: 'Regulatory fines', description: 'Non-compliance with new regional data localization laws.', status: 'Closed', priority: 'High', date: '2025-11-05', createdAt: '2025-11-05T10:10:00.000Z' },
  { id: 'r12', name: 'Malware infection', description: 'Lack of endpoint protection on contractor devices.', status: 'Closed', priority: 'Medium', date: '2025-10-12', createdAt: '2025-10-12T13:30:00.000Z' },
  { id: 'r13', name: 'Supply chain disruption', description: 'Hardware procurement delays impacting project timelines.', status: 'Open', priority: 'Low', date: '2026-04-02', createdAt: '2026-04-02T09:05:00.000Z' },
  { id: 'r14', name: 'Incomplete employee offboarding', description: 'Former employees retaining access to internal systems.', status: 'Active', priority: 'High', date: '2026-03-22', createdAt: '2026-03-22T15:50:00.000Z' },
  { id: 'r15', name: 'API rate limiting failures', description: 'Public endpoints vulnerable to scraping or DDoS.', status: 'In Progress', priority: 'Medium', date: '2026-01-30', createdAt: '2026-01-30T10:25:00.000Z' },
  { id: 'r16', name: 'Outdated cryptographic protocols', description: 'Use of TLS 1.1 in legacy microservices.', status: 'Closed', priority: 'Medium', date: '2025-09-18', createdAt: '2025-09-18T14:40:00.000Z' },
  { id: 'r17', name: 'Inaccurate financial reporting', description: 'Manual data entry errors in end-of-month ledger processing.', status: 'Active', priority: 'High', date: '2026-04-20', createdAt: '2026-04-20T11:15:00.000Z' },
  { id: 'r18', name: 'Lack of code reviews', description: 'Junior developers pushing directly to main branch.', status: 'Closed', priority: 'Low', date: '2025-12-01', createdAt: '2025-12-01T09:55:00.000Z' },
  { id: 'r19', name: 'Stale SSH keys', description: 'Long-lived credentials not rotated regularly.', status: 'In Progress', priority: 'Medium', date: '2026-03-10', createdAt: '2026-03-10T16:05:00.000Z' },
  { id: 'r20', name: 'Insufficient logging', description: 'Cannot reliably reconstruct events during security incidents.', status: 'Open', priority: 'High', date: '2026-01-15', createdAt: '2026-01-15T08:30:00.000Z' },
  { id: 'r21', name: 'Unencrypted backups', description: 'Database snapshots stored in plain text on external drives.', status: 'Active', priority: 'High', date: '2026-04-05', createdAt: '2026-04-05T12:00:00.000Z' },
  { id: 'r22', name: 'Shadow IT usage', description: 'Departments using unauthorized SaaS tools for sensitive data.', status: 'Open', priority: 'Medium', date: '2026-02-22', createdAt: '2026-02-22T14:20:00.000Z' },
  { id: 'r23', name: 'Expired SSL certificates', description: 'Internal admin portals returning browser warnings.', status: 'Closed', priority: 'Medium', date: '2025-11-30', createdAt: '2025-11-30T09:45:00.000Z' },
  { id: 'r24', name: 'Poor password hygiene', description: 'Users selecting easily guessable passwords.', status: 'In Progress', priority: 'Low', date: '2026-03-28', createdAt: '2026-03-28T10:35:00.000Z' },
  { id: 'r25', name: 'Third-party API deprecation', description: 'Payment gateway API version ending support soon.', status: 'Active', priority: 'High', date: '2026-04-25', createdAt: '2026-04-25T15:10:00.000Z' },
]

function extractErrorMessage(error) {
  const status = error?.response?.status
  const data = error?.response?.data

  const messageFromData =
    typeof data === 'string'
      ? data
      : data?.message || data?.error || data?.detail || data?.msg

  if (messageFromData) return String(messageFromData)

  if (status === 401) return 'Session expired. Please log in again.'
  if (status === 403) return 'You do not have permission to perform this action.'
  if (status === 404) return 'Requested resource was not found.'

  if (error?.message) return String(error.message)
  return 'Something went wrong. Please try again.'
}

function isBackendDown(error) {
  // No response means network/connection error (server down).
  if (!error?.response) return true
  // If server responded with a 5xx, treat it as backend failure.
  const status = error?.response?.status
  if (typeof status === 'number' && status >= 500) return true
  return false
}

function createAxiosLikeError(status, message) {
  return {
    response: {
      status,
      data: { message },
    },
    message: String(message),
  }
}

function parseBody(data) {
  if (!data) return {}
  if (typeof data === 'string') {
    try {
      return JSON.parse(data)
    } catch {
      return {}
    }
  }
  return data
}

function normalizeDateForCompare(value) {
  if (!value) return null
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return null
  return d
}

function getRiskDate(r) {
  return r?.createdAt || r?.date || r?.created_at || r?.createdDate || null
}

function filterAndPaginateRisks(params) {
  const search = String(params?.q || params?.search || '').trim().toLowerCase()
  const status = params?.status ? String(params.status) : 'All'

  const start = normalizeDateForCompare(params?.startDate)
  const end = normalizeDateForCompare(params?.endDate)

  let filtered = [...mockRisks]

  if (search) {
    filtered = filtered.filter((r) => {
      const haystack = `${r.name || ''} ${r.description || ''}`.toLowerCase()
      return haystack.includes(search)
    })
  }

  if (status && status !== 'All') {
    filtered = filtered.filter((r) => String(r.status || '') === status)
  }

  if (start || end) {
    filtered = filtered.filter((r) => {
      const rd = normalizeDateForCompare(getRiskDate(r))
      if (!rd) return false
      if (start && rd < start) return false
      if (end) {
        // Make `endDate` inclusive of the selected day.
        const inclusiveEnd = new Date(end)
        inclusiveEnd.setHours(23, 59, 59, 999)
        if (rd > inclusiveEnd) return false
      }
      return true
    })
  }

  const sortBy = String(params?.sortBy || 'date')
  const sortDir = String(params?.sortDir || 'desc').toLowerCase()
  const size = Number(params?.size || 10)
  const page = Number(params?.page || 1)

  const safeSize = Number.isNaN(size) || size <= 0 ? 10 : size
  const safePage = Number.isNaN(page) || page <= 0 ? 1 : page

  // Sorting (mock mode) so UI controls behave the same.
  const dirFactor = sortDir === 'asc' ? 1 : -1
  filtered.sort((a, b) => {
    if (sortBy === 'name') {
      return String(a.name || '').localeCompare(String(b.name || '')) * dirFactor
    }
    if (sortBy === 'status') {
      return String(a.status || '').localeCompare(String(b.status || '')) * dirFactor
    }
    // Default: date
    const ad = normalizeDateForCompare(getRiskDate(a))?.getTime?.() || 0
    const bd = normalizeDateForCompare(getRiskDate(b))?.getTime?.() || 0
    return (ad - bd) * dirFactor
  })

  const totalPages = Math.max(1, Math.ceil(filtered.length / safeSize))
  const startIdx = (safePage - 1) * safeSize
  const items = filtered.slice(startIdx, startIdx + safeSize)

  return { items, totalPages }
}

function extractStatusCounts() {
  const counts = {}
  for (const r of mockRisks) {
    const s = String(r.status || 'Unknown')
    counts[s] = (counts[s] || 0) + 1
  }
  return Object.entries(counts)
    .map(([status, count]) => ({ status, count }))
    .sort((a, b) => b.count - a.count)
}

function extractOverTime() {
  // Group risks by month.
  const byMonth = new Map()
  for (const r of mockRisks) {
    const d = normalizeDateForCompare(getRiskDate(r))
    if (!d) continue
    const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
    byMonth.set(key, (byMonth.get(key) || 0) + 1)
  }

  return [...byMonth.entries()]
    .map(([key, count]) => ({ date: `${key}-01`, count }))
    .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
}

function buildStats() {
  const totalRisks = mockRisks.length
  const activeRisks = mockRisks.filter(
    (r) => String(r.status || '').toLowerCase() !== 'closed',
  ).length
  const closedRisks = mockRisks.filter((r) => String(r.status || '').toLowerCase() === 'closed')
    .length
  const highPriorityRisks = mockRisks.filter(
    (r) => String(r.priority || '').toLowerCase() === 'high',
  ).length

  return {
    totalRisks,
    activeRisks,
    closedRisks,
    highPriorityRisks,
    risksByStatus: extractStatusCounts(),
    overTime: extractOverTime(),
  }
}

function riskDetailById(id) {
  const risk = mockRisks.find((r) => String(r.id) === String(id))
  if (!risk) throw createAxiosLikeError(404, 'Risk not found')
  return risk
}

function createMockResponse(config, data, headers = {}) {
  return Promise.resolve({
    data,
    status: 200,
    statusText: 'OK',
    headers,
    config,
  })
}

export const api = axios.create({
  baseURL: BASE_URL,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // If backend is down, switch to mock mode for known endpoints.
    if (isBackendDown(error)) {
      const { config } = error || {}
      const method = String(config?.method || '').toLowerCase()
      const url = String(config?.url || '')

      // Normalize URL (axios keeps `url` as passed, often with a leading slash).
      const path = url.startsWith('/') ? url : `/${url}`

      // AUTH
      if (method === 'post' && path === '/auth/login') {
        return createMockResponse(config, { token: MOCK.token })
      }

      // DASHBOARD + ANALYTICS
      if (method === 'get' && path === '/stats') {
        return createMockResponse(config, buildStats())
      }

      // LIST
      if (method === 'get' && path === '/all') {
        const { params } = config || {}
        return createMockResponse(config, filterAndPaginateRisks(params || {}))
      }

      // EXPORT (CSV)
      if (method === 'get' && path === '/export') {
        const { params } = config || {}
        // Export the filtered set without pagination.
        const paged = filterAndPaginateRisks({ ...params, page: 1, size: 100000 })
        const items = paged?.items || []
        const header = ['Name', 'Status', 'Date', 'Description']
        const escape = (v) => {
          const s = String(v ?? '')
          if (s.includes('"') || s.includes(',') || s.includes('\n')) return `"${s.replaceAll('"', '""')}"`
          return s
        }
        const rows = items.map((r) => [
          escape(r.name),
          escape(r.status),
          escape((r.date || r.createdAt || '').toString().slice(0, 10)),
          escape(r.description),
        ])
        const csv = [header.join(','), ...rows.map((row) => row.join(','))].join('\n')
        return createMockResponse(
          config,
          csv,
          { 'content-disposition': 'attachment; filename="risk-export.csv"' },
        )
      }

      // CREATE
      if (method === 'post' && path === '/create') {
        const body = parseBody(config?.data)
        const next = {
          id: `r${mockRisks.length + 1}`,
          name: String(body?.name || '').trim(),
          description: String(body?.description || ''),
          status: String(body?.status || '').trim(),
          // Default priority for mock mode.
          priority: 'Low',
          date: new Date().toISOString().slice(0, 10),
          createdAt: new Date().toISOString(),
        }
        if (!next.name || !next.status) {
          return Promise.reject(createAxiosLikeError(400, 'Name and status are required.'))
        }
        mockRisks = [next, ...mockRisks]
        return createMockResponse(config, next)
      }

      // DETAIL / UPDATE / DELETE by ID
      const idMatch = path.match(/^\/([^/]+)$/)
      if (idMatch) {
        const id = idMatch[1]
        if (method === 'get') {
          try {
            return createMockResponse(config, riskDetailById(id))
          } catch (e) {
            return Promise.reject(e)
          }
        }
        if (method === 'put') {
          const body = parseBody(config?.data)
          const idx = mockRisks.findIndex((r) => String(r.id) === String(id))
          if (idx === -1) return Promise.reject(createAxiosLikeError(404, 'Risk not found'))
          mockRisks = [
            ...mockRisks.slice(0, idx),
            {
              ...mockRisks[idx],
              name: body?.name !== undefined ? body.name : mockRisks[idx].name,
              description:
                body?.description !== undefined ? body.description : mockRisks[idx].description,
              status: body?.status !== undefined ? body.status : mockRisks[idx].status,
            },
            ...mockRisks.slice(idx + 1),
          ]
          return createMockResponse(config, mockRisks[idx])
        }
        if (method === 'delete') {
          const before = mockRisks.length
          mockRisks = mockRisks.filter((r) => String(r.id) !== String(id))
          if (mockRisks.length === before) return Promise.reject(createAxiosLikeError(404, 'Risk not found'))
          return createMockResponse(config, { success: true })
        }
      }

      // AI endpoints (used in Risk Detail)
      if (method === 'post' && path === '/describe') {
        const body = parseBody(config?.data)
        const risk = body?.risk || {}
        const name = risk?.name || 'this risk'
        const status = risk?.status || 'Unknown'
        return createMockResponse(config, {
          description: `Risk "${name}" is currently marked as "${status}". Focus on mitigation steps that reduce likelihood and impact, and define clear ownership and timelines.`,
        })
      }

      if (method === 'post' && path === '/recommend') {
        const body = parseBody(config?.data)
        const risk = body?.risk || {}
        const name = risk?.name || 'the risk'
        const status = risk?.status || 'Unknown'

        const recommendations = [
          `Conduct a root-cause assessment for "${name}" and document findings.`,
          `Define a mitigation plan aligned to "${status}" status, including measurable success criteria.`,
          'Assign an owner and set a review cadence to track progress.',
          'Implement monitoring/controls to catch early warning signals.',
        ]

        return createMockResponse(config, { recommendations })
      }

      // Unknown endpoint: fail normally with a friendly message.
      const message = extractErrorMessage(error)
      window.dispatchEvent(
        new CustomEvent('api:error', {
          detail: { message, status: error?.response?.status },
        }),
      )
      return Promise.reject(error)
    }

    // Backend responded, but request failed (validation/4xx etc).
    const message = extractErrorMessage(error)

    if (error?.response?.status === 401) {
      localStorage.removeItem('token')
      window.dispatchEvent(new Event('auth:logout'))
    }

    window.dispatchEvent(
      new CustomEvent('api:error', { detail: { message, status: error?.response?.status } }),
    )

    return Promise.reject(error)
  },
)

