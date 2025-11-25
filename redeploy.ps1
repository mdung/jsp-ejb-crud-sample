# Force Redeploy Script - Removes old deployment and deploys new one
# This ensures WildFly picks up the new WAR file

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "FORCE REDEPLOY - Remove Old & Deploy New" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$deployDir = "C:\wildfly-38.0.1.Final\standalone\deployments"
$warFile = "dist\employee-demo.war"

# Step 1: Check if WAR exists
if (-not (Test-Path $warFile)) {
    Write-Host "❌ ERROR: WAR file not found: $warFile" -ForegroundColor Red
    Write-Host "Please build the application first: .\build-windows.bat" -ForegroundColor Yellow
    exit 1
}

Write-Host "[1/4] Checking WAR file..." -ForegroundColor Cyan
$warInfo = Get-Item $warFile
Write-Host "   WAR file: $warFile" -ForegroundColor White
Write-Host "   Size: $([math]::Round($warInfo.Length / 1KB, 2)) KB" -ForegroundColor White
Write-Host "   Modified: $($warInfo.LastWriteTime)" -ForegroundColor White
Write-Host ""

# Step 2: Remove ALL old deployment files
Write-Host "[2/4] Removing old deployment..." -ForegroundColor Cyan
$oldFiles = Get-ChildItem "$deployDir\employee-demo.war*" -ErrorAction SilentlyContinue
if ($oldFiles) {
    foreach ($file in $oldFiles) {
        Write-Host "   Removing: $($file.Name)" -ForegroundColor Yellow
        Remove-Item $file.FullName -Force -ErrorAction SilentlyContinue
    }
    Write-Host "   ✅ Old deployment removed" -ForegroundColor Green
} else {
    Write-Host "   ℹ️  No old deployment found" -ForegroundColor Gray
}
Write-Host ""

# Step 3: Wait a moment for WildFly to process removal
Write-Host "[3/4] Waiting for WildFly to process removal..." -ForegroundColor Cyan
Start-Sleep -Seconds 3
Write-Host ""

# Step 4: Copy new WAR file
Write-Host "[4/4] Deploying new WAR file..." -ForegroundColor Cyan
try {
    Copy-Item $warFile "$deployDir\employee-demo.war" -Force
    Write-Host "   ✅ WAR file copied to deployments folder" -ForegroundColor Green
} catch {
    Write-Host "   ❌ ERROR copying WAR file: $_" -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 5: Wait for deployment
Write-Host "Waiting for WildFly to deploy..." -ForegroundColor Yellow
Start-Sleep -Seconds 8
Write-Host ""

# Step 6: Check deployment status
Write-Host "Checking deployment status..." -ForegroundColor Cyan
$deployed = Test-Path "$deployDir\employee-demo.war.deployed"
$failed = Test-Path "$deployDir\employee-demo.war.failed"
$pending = Test-Path "$deployDir\employee-demo.war.isdeploying"

if ($deployed) {
    Write-Host ""
    Write-Host "✅ DEPLOYMENT SUCCESSFUL!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Access the application at:" -ForegroundColor Cyan
    Write-Host "  - Employee List: http://localhost:8080/employee-demo/employee-list.xhtml" -ForegroundColor Yellow
    Write-Host "  - Home: http://localhost:8080/employee-demo/index.xhtml" -ForegroundColor Yellow
    Write-Host ""
    
    # Test the application
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/employee-demo/employee-list.xhtml" -UseBasicParsing -TimeoutSec 5
        Write-Host "✅ Application is responding! (HTTP $($response.StatusCode))" -ForegroundColor Green
    } catch {
        Write-Host "⚠️  Application deployed but not responding yet. Wait a few seconds and try again." -ForegroundColor Yellow
    }
} elseif ($failed) {
    Write-Host ""
    Write-Host "❌ DEPLOYMENT FAILED" -ForegroundColor Red
    Write-Host "Check WildFly logs: C:\wildfly-38.0.1.Final\standalone\log\server.log" -ForegroundColor Yellow
} elseif ($pending) {
    Write-Host ""
    Write-Host "⏳ Deployment in progress... Check again in a few seconds." -ForegroundColor Yellow
} else {
    Write-Host ""
    Write-Host "⚠️  Deployment status unclear. Check WildFly logs." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Deployment files:" -ForegroundColor Cyan
$deployFiles = Get-ChildItem -Path $deployDir -Filter "employee-demo.war*"
$deployFiles | Format-Table Name, LastWriteTime -AutoSize
Write-Host ""

