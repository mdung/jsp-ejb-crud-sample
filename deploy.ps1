# Quick Deployment Script
# Run this AFTER restarting WildFly

Write-Host "Deploying employee-demo.war..." -ForegroundColor Cyan

# Remove old deployment
Remove-Item "C:\wildfly-38.0.1.Final\standalone\deployments\employee-demo.war*" -Force -ErrorAction SilentlyContinue

# Copy WAR file
Copy-Item "dist\employee-demo.war" "C:\wildfly-38.0.1.Final\standalone\deployments\"

Write-Host "Waiting for deployment..." -ForegroundColor Yellow
Start-Sleep -Seconds 6

# Check deployment status
$deployed = Test-Path "C:\wildfly-38.0.1.Final\standalone\deployments\employee-demo.war.deployed"
$failed = Test-Path "C:\wildfly-38.0.1.Final\standalone\deployments\employee-demo.war.failed"

if ($deployed) {
    Write-Host "`n✅ DEPLOYMENT SUCCESSFUL!" -ForegroundColor Green
    Write-Host "`nAccess the application at:" -ForegroundColor Cyan
    Write-Host "JSF Application:" -ForegroundColor Yellow
    Write-Host "  - Employee List: http://localhost:8080/employee-demo/employee-list.xhtml" -ForegroundColor Yellow
    Write-Host "  - Home: http://localhost:8080/employee-demo/index.xhtml" -ForegroundColor Yellow
    
    # Test the application
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/employee-demo/employee-list.xhtml" -UseBasicParsing -TimeoutSec 5
        Write-Host "`n✅ Application is responding! (HTTP $($response.StatusCode))" -ForegroundColor Green
    } catch {
        Write-Host "`n⚠️  Application deployed but not responding yet. Wait a few seconds and try again." -ForegroundColor Yellow
    }
} elseif ($failed) {
    Write-Host "`n❌ DEPLOYMENT FAILED" -ForegroundColor Red
    Write-Host "Check WildFly logs: C:\wildfly-38.0.1.Final\standalone\log\server.log" -ForegroundColor Yellow
} else {
    Write-Host "`n⏳ Deployment in progress... Check again in a few seconds." -ForegroundColor Yellow
}

Write-Host "`nDeployment files:" -ForegroundColor Cyan
Get-ChildItem "C:\wildfly-38.0.1.Final\standalone\deployments\employee-demo.war*" | Format-Table Name, LastWriteTime


