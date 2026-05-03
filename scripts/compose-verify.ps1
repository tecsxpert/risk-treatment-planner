# Full stack: tear down volumes, rebuild, start, then smoke-test OpenAPI.
# Requires Docker Desktop (or docker on PATH) and a .env next to docker-compose.yml.
$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error "Docker CLI not on PATH. Install Docker Desktop, restart the terminal, then re-run: scripts\compose-verify.ps1"
}

docker compose down -v
docker compose up --build -d

$port = 8080
if (Test-Path (Join-Path $root ".env")) {
    Get-Content (Join-Path $root ".env") | ForEach-Object {
        if ($_ -match '^\s*BACKEND_PORT\s*=\s*(\d+)\s*$') { $port = $matches[1] }
    }
}

$url = "http://127.0.0.1:$port/v3/api-docs"
Write-Host "Waiting for API at $url ..."
$ok = $false
for ($i = 0; $i -lt 36; $i++) {
    try {
        $r = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 5
        if ($r.StatusCode -eq 200) { $ok = $true; break }
    } catch {}
    Start-Sleep -Seconds 5
}
if (-not $ok) {
    docker compose ps
    docker compose logs risk-backend --tail 80
    Write-Error "Backend did not respond at $url within ~3 minutes."
}

Write-Host "OK: $url returned 200. Stack is up (postgres, redis, pgadmin, backend, nginx placeholder)."
Write-Host "Swagger UI: http://127.0.0.1:$port/swagger-ui/index.html"
