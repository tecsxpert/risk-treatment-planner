Write-Host "🚀 Starting Day 17 Rehearsal Scenarios..." -ForegroundColor Cyan

# Scenario 1: Check if API is alive
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/v3/api-docs" -UseBasicParsing
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Scenario 1: Backend Transition Success (API is Online)" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Scenario 1: Backend Transition Failed (Check if Docker is running)" -ForegroundColor Red
}

# Scenario 2: Test Database Connectivity via Health Actuator
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health"
    if ($health.status -eq "UP") {
        Write-Host "✅ Scenario 2: Data Integrity Verified (DB is Connected)" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Scenario 2: Data Integrity Failed" -ForegroundColor Red
}

Write-Host "🏁 Rehearsal Complete." -ForegroundColor Cyan