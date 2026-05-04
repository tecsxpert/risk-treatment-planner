import { useCallback, useEffect, useMemo, useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { api } from '../services/api'
import ConfirmDialog from '../components/ConfirmDialog'
import EmptyState from '../components/EmptyState'
import Loader from '../components/Loader'
import PaginationControls from '../components/PaginationControls'
import RiskTable from '../components/RiskTable'
import SearchAndFilters from '../components/SearchAndFilters'
import ErrorBanner from '../components/ErrorBanner'
import Card from '../components/Card'
import Button from '../components/Button'
import useDebouncedValue from '../hooks/useDebouncedValue'

function getId(risk) {
  return risk?.id ?? risk?._id ?? risk?.riskId
}

function extractPaginated(data) {
  if (Array.isArray(data)) return { items: data, totalPages: 1 }
  const root = data?.data ?? data

  const items =
    root?.content ??
    root?.items ??
    root?.results ??
    root?.rows ??
    root?.data ??
    []

  const totalPages =
    root?.totalPages ??
    root?.pageCount ??
    root?.total_pages ??
    (root?.totalElements
      ? Math.max(1, Math.ceil(Number(root.totalElements) / Number(root.size || 1)))
      : 1)

  return { items, totalPages: Number(totalPages) || 1 }
}

function escapeCsvValue(value) {
  const s = String(value ?? '')
  if (s.includes('"') || s.includes(',') || s.includes('\n')) {
    return `"${s.replaceAll('"', '""')}"`
  }
  return s
}

export default function RiskListPage() {
  const navigate = useNavigate()
  const [searchParams, setSearchParams] = useSearchParams()

  const rawPage = Number(searchParams.get('page') || 1)
  const page = Number.isNaN(rawPage) || rawPage < 1 ? 1 : rawPage

  const rawSize = Number(searchParams.get('size') || 10)
  const size = Number.isNaN(rawSize)
    ? 10
    : Math.max(5, Math.min(50, rawSize))

  const sortBy = searchParams.get('sortBy') || 'date'
  const sortDir = searchParams.get('sortDir') === 'asc' ? 'asc' : 'desc'

  const status = searchParams.get('status') || 'All'
  const startDate = searchParams.get('startDate') || ''
  const endDate = searchParams.get('endDate') || ''

  const qFromUrl = searchParams.get('q') ?? searchParams.get('search') ?? ''

  const [searchInput, setSearchInput] = useState(qFromUrl)

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setSearchInput(qFromUrl)
  }, [qFromUrl])

  const debouncedQ = useDebouncedValue(searchInput, 300)

  const [risks, setRisks] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [totalPages, setTotalPages] = useState(1)

  const [exporting, setExporting] = useState(false)
  const [refreshKey, setRefreshKey] = useState(0)

  const [deleteOpen, setDeleteOpen] = useState(false)
  const [deleting, setDeleting] = useState(false)
  const [riskToDelete, setRiskToDelete] = useState(null)

  const params = useMemo(() => {
    const p = {
      page,
      size,
      sortBy,
      sortDir,
    }

    // Requirement: GET /all?q=...
    const trimmedQ = String(debouncedQ || '').trim()
    if (trimmedQ) p.q = trimmedQ

    if (status && status !== 'All') p.status = status
    if (startDate) p.startDate = startDate
    if (endDate) p.endDate = endDate

    return p
  }, [page, size, sortBy, sortDir, debouncedQ, status, startDate, endDate])

  useEffect(() => {
    let mounted = true
    async function load() {
      setLoading(true)
      setError('')
      try {
        const res = await api.get('/all', { params })
        if (!mounted) return
        const payload = res?.data?.data || res?.data?.content || res?.data || []
        const extracted = extractPaginated(payload)
        setRisks(Array.isArray(extracted.items) ? extracted.items : [])
        setTotalPages(extracted.totalPages)
      } catch (e) {
        if (!mounted) return
        const message =
          e?.response?.data?.message ||
          e?.response?.data?.error ||
          (typeof e?.response?.data === 'string' ? e.response.data : null) ||
          e?.message ||
          'Failed to load risk list.'
        setError(String(message))
        setRisks([])
        setTotalPages(1)
      } finally {
        if (mounted) setLoading(false)
      }
    }

    load()
    return () => {
      mounted = false
    }
  }, [params, refreshKey])

  function updateUrl(next) {
    const nextParams = new URLSearchParams(searchParams)
    Object.entries(next).forEach(([k, v]) => {
      if (v === undefined || v === null || v === '') nextParams.delete(k)
      else nextParams.set(k, String(v))
    })
    setSearchParams(nextParams, { replace: true })
  }

  function handleSearchChange(nextVal) {
    setSearchInput(nextVal)
    updateUrl({
      q: nextVal.trim() || undefined,
      page: 1,
    })
  }

  const handleView = useCallback((risk) => {
    const id = getId(risk)
    if (id) navigate(`/detail/${id}`)
  }, [navigate])

  const handleEdit = useCallback((risk) => {
    const id = getId(risk)
    if (id) navigate(`/edit/${id}`)
  }, [navigate])

  const handleDeleteRequest = useCallback((risk) => {
    setRiskToDelete(risk)
    setDeleteOpen(true)
  }, [])

  async function handleDelete() {
    if (!riskToDelete) return
    const id = getId(riskToDelete)
    if (!id) {
      setDeleteOpen(false)
      setRiskToDelete(null)
      return
    }

    setDeleting(true)
    setError('')
    try {
      await api.delete(`/${id}`)
      setDeleteOpen(false)
      setRiskToDelete(null)
      setRefreshKey((k) => k + 1)
    } catch (e) {
      const message =
        e?.response?.data?.message ||
        e?.response?.data?.error ||
        (typeof e?.response?.data === 'string' ? e.response.data : null) ||
        e?.message ||
        'Failed to delete risk.'
      setError(String(message))
      setDeleteOpen(false)
      setRiskToDelete(null)
    } finally {
      setDeleting(false)
    }
  }

  async function exportCsv() {
    setExporting(true)
    try {
      const res = await api.get('/export', {
        responseType: 'blob',
        params: {
          ...(debouncedQ?.trim() ? { q: debouncedQ.trim() } : {}),
          ...(status && status !== 'All' ? { status } : {}),
          ...(startDate ? { startDate } : {}),
          ...(endDate ? { endDate } : {}),
          sortBy,
          sortDir,
        },
      })

      const blob = new Blob([res.data], { type: 'text/csv' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url

      const disposition = res.headers?.['content-disposition']
      const filename =
        disposition?.match(/filename="?([^"]+)"?/)?.[1] || 'risk-export.csv'
      a.download = filename
      document.body.appendChild(a)
      a.click()
      a.remove()
      window.URL.revokeObjectURL(url)
    } catch (e) {
      // Keep UI usable: we generate a CSV from the current list if export fails.
      const message =
        e?.response?.data?.message ||
        e?.message ||
        'Export API failed. Using CSV fallback.'
      window.dispatchEvent(
        new CustomEvent('api:error', { detail: { message: String(message) } }),
      )

      // Fallback: generate CSV from the current list shown.
      const header = ['Name', 'Status', 'Date', 'Description']
      const rows = risks.map((r) => [
        escapeCsvValue(r.name),
        escapeCsvValue(r.status),
        escapeCsvValue(String(r.date || r.createdAt || '').slice(0, 10)),
        escapeCsvValue(r.description),
      ])
      const csv = [header.join(','), ...rows.map((row) => row.join(','))].join('\n')

      const blob = new Blob([csv], { type: 'text/csv' })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'risk-export.csv'
      document.body.appendChild(a)
      a.click()
      a.remove()
      window.URL.revokeObjectURL(url)
    } finally {
      setExporting(false)
    }
  }

  return (
    <div className="space-y-5">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-[#1B4F8A]">Risk List</h1>
          <p className="mt-1 text-sm text-gray-600">Search, filter, and manage your risks.</p>
        </div>

        <div className="flex flex-wrap items-center gap-2">
          <Button
            variant="secondary"
            onClick={exportCsv}
            disabled={exporting || loading}
            className="shadow-sm ring-1 ring-gray-200"
          >
            {exporting ? 'Exporting…' : 'Export CSV'}
          </Button>
          <Button
            variant="primary"
            onClick={() => navigate('/create')}
          >
            + Create
          </Button>
        </div>
      </div>

      <div className="grid gap-3 lg:grid-cols-2">
        <SearchAndFilters
          search={searchInput}
          onSearchChange={handleSearchChange}
          status={status}
          onStatusChange={(nextStatus) => updateUrl({ status: nextStatus, page: 1 })}
          startDate={startDate}
          onStartDateChange={(nextStart) => updateUrl({ startDate: nextStart, page: 1 })}
          endDate={endDate}
          onEndDateChange={(nextEnd) => updateUrl({ endDate: nextEnd, page: 1 })}
        />

        <Card className="p-4">
          <div className="text-sm font-medium text-gray-700">Pagination & Sorting</div>
          <div className="mt-3 grid gap-3 sm:grid-cols-3">
            <div>
              <label className="mb-1 block text-xs font-medium text-gray-600">Page size</label>
              <select
                value={size}
                onChange={(e) =>
                  updateUrl({
                    size: e.target.value,
                    page: 1,
                  })
                }
                className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm outline-none focus:border-[#1B4F8A] focus:ring-2 focus:ring-[#1B4F8A]/20"
              >
                {[5, 10, 20, 50].map((n) => (
                  <option key={n} value={n}>
                    {n}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="mb-1 block text-xs font-medium text-gray-600">Sort by</label>
              <select
                value={sortBy}
                onChange={(e) => updateUrl({ sortBy: e.target.value, page: 1 })}
                className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm outline-none focus:border-[#1B4F8A] focus:ring-2 focus:ring-[#1B4F8A]/20"
              >
                <option value="date">Date</option>
                <option value="name">Name</option>
                <option value="status">Status</option>
              </select>
            </div>

            <div>
              <label className="mb-1 block text-xs font-medium text-gray-600">Direction</label>
              <select
                value={sortDir}
                onChange={(e) => updateUrl({ sortDir: e.target.value, page: 1 })}
                className="w-full rounded-md border border-gray-200 bg-white px-3 py-2 text-sm outline-none focus:border-[#1B4F8A] focus:ring-2 focus:ring-[#1B4F8A]/20"
              >
                <option value="asc">Ascending</option>
                <option value="desc">Descending</option>
              </select>
            </div>
          </div>
        </Card>
      </div>

      {loading ? (
        <Loader label="Loading risks…" />
      ) : error ? (
        <ErrorBanner message={error} title="Could not load risks" />
      ) : risks.length ? (
        <>
          <RiskTable
            risks={risks}
            onView={handleView}
            onEdit={handleEdit}
            onDelete={handleDeleteRequest}
          />

          <PaginationControls
            page={page}
            totalPages={totalPages}
            onPageChange={(next) => updateUrl({ page: next })}
          />
        </>
      ) : (
        <EmptyState
          title="No risks found"
          description="Try clearing your search or changing the filters."
        />
      )}

      <ConfirmDialog
        open={deleteOpen}
        title="Delete this risk?"
        description="This will soft delete the risk. (Mock mode removes it from the list.)"
        confirmLabel="Delete"
        loading={deleting}
        onClose={() => {
          setDeleteOpen(false)
          setRiskToDelete(null)
        }}
        onConfirm={handleDelete}
      />
    </div>
  )
}

