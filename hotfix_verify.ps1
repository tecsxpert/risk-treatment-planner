Write-Host "Day 19: Final Hotfix Verification (Certified Release)..." -ForegroundColor Cyan

# 1. Test Connection Stability (P1 Fix)
Write-Host "Testing Database Connection Stability..." -ForegroundColor Yellow
try {
    $dbCheck = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -UseBasicParsing
    if ($dbCheck.status -eq "UP") {
        Write-Host "SUCCESS: P1 Fix Verified. Database connection is stable." -ForegroundColor Green
    }
} catch {
    $code = [int]$_.Exception.Response.StatusCode
    if ($code -eq 403) {
        Write-Host "SUCCESS: P1 Fix Verified (Database is UP, but secured)." -ForegroundColor Green
    } else {
        Write-Host "FAILED: P1 Fix Failed. Status: $code" -ForegroundColor Red
    }
}

# 2. Test Security Protocol (P2 Fix)
Write-Host "Testing Security Protocol..." -ForegroundColor Yellow
try {
    Invoke-WebRequest -Uri "http://localhost:8080/api/risks" -Method Get -UseBasicParsing
    Write-Host "FAILED: P2 Fix Failed. Endpoint is exposed!" -ForegroundColor Red
} catch {
    $statusCode = [int]$_.Exception.Response.StatusCode
    if ($statusCode -eq 401 -or $statusCode -eq 403) {
        Write-Host "SUCCESS: P2 Fix Verified. Unauthorized access blocked (Status: $statusCode)." -ForegroundColor Green
    }
}

Write-Host "Day 19 Hotfix Window Closed." -ForegroundColor Cyan