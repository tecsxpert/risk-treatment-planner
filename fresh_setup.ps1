Write-Host "Day 18: Starting Optimized Fresh Machine Test..." -ForegroundColor Cyan

# 1. Create .env file automatically if it doesn't exist
if (-not (Test-Path .env)) {
    Write-Host "Creating .env file with default configurations..." -ForegroundColor Yellow
    $envContent = @"
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password123
POSTGRES_DB=risk_db
POSTGRES_PORT=5432
REDIS_PORT=6379
PGADMIN_PORT=5050
PGADMIN_DEFAULT_EMAIL=admin@risk.com
PGADMIN_DEFAULT_PASSWORD=admin
BACKEND_PORT=8080
FRONTEND_PORT=3000
JWT_SECRET=YourSuperSecretKeyForDay18FinalTest12345
"@
    Set-Content -Path .env -Value $envContent
}

# 2. Start the Clock
$startTime = Get-Date
Write-Host "Timer Started. Building and starting containers..." -ForegroundColor Yellow

# 3. Docker Compose Build and Up
# Using --quiet-pull to reduce terminal noise which can slightly slow down execution
docker-compose up --build -d

# 4. Wait for Health Checks (Check every 2 seconds for precision)
Write-Host "Waiting for 5 services to reach healthy status..." -ForegroundColor Yellow
$timeout = 180 
$elapsed = 0
while ($elapsed -lt $timeout) {
    $status = docker ps --filter "health=healthy" --format "{{.Names}}"
    $count = ($status | Measure-Object -Line).Lines
    
    # Show progress in terminal
    Write-Progress -Activity "Deploying Full Stack" -Status "$count/5 Services Healthy" -PercentComplete (($count / 5) * 100)
    
    if ($count -ge 5) { break } 
    
    Start-Sleep -Seconds 2
    $elapsed += 2
}

# 5. Final Report
$endTime = Get-Date
$duration = ($endTime - $startTime).TotalSeconds
Write-Host "`nFresh Machine Test Complete!" -ForegroundColor Cyan
Write-Host "Total Time: $duration seconds" -ForegroundColor Green

if ($duration -lt 180) {
    Write-Host "SUCCESS: Full stack healthy in $duration seconds!" -ForegroundColor Green
} else {
    Write-Host "WARNING: Setup took $duration seconds. See optimization tips below." -ForegroundColor Red
}