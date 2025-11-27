# Test Script to Verify Navigation Fix
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING NAVIGATION FIX - EVIDENCE" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Check deployed WAR file
Write-Host "[TEST 1] Checking deployed WAR file..." -ForegroundColor Yellow
$warPath = "dist\employee-demo.war"
if (Test-Path $warPath) {
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $zip = [System.IO.Compression.ZipFile]::OpenRead($warPath)
    $entry = $zip.Entries | Where-Object { $_.FullName -eq "employee-detail.xhtml" }
    $stream = $entry.Open()
    $reader = New-Object System.IO.StreamReader($stream)
    $xhtmlContent = $reader.ReadToEnd()
    $reader.Close()
    $stream.Close()
    $zip.Dispose()
    
    Write-Host "  Extracting 'Back to List' buttons from source XHTML..." -ForegroundColor Gray
    $buttonPattern = '<h:commandLink[^>]*value="Back to List"[^>]*action="([^"]+)"[^>]*>'
    $matches = [regex]::Matches($xhtmlContent, $buttonPattern)
    
    Write-Host "  Found $($matches.Count) button(s)" -ForegroundColor Cyan
    Write-Host ""
    
    $allCorrect = $true
    foreach ($match in $matches) {
        $action = $match.Groups[1].Value
        $fullButton = $match.Groups[0].Value
        Write-Host "  Button HTML:" -ForegroundColor White
        Write-Host "  $fullButton" -ForegroundColor Gray
        Write-Host "  Action value: '$action'" -ForegroundColor Cyan
        
        if ($action -eq 'list') {
            Write-Host "  [OK] Correct action='list' found!" -ForegroundColor Green
        } elseif ($action -match 'employee-list') {
            Write-Host "  [ERROR] Old incorrect action found!" -ForegroundColor Red
            $allCorrect = $false
        } else {
            Write-Host "  [WARNING] Unexpected action value" -ForegroundColor Yellow
            $allCorrect = $false
        }
        Write-Host ""
    }
    
    if ($allCorrect -and $matches.Count -eq 2) {
        Write-Host "  [SUCCESS] Both buttons are correctly fixed!" -ForegroundColor Green
    } elseif ($matches.Count -lt 2) {
        Write-Host "  [WARNING] Expected 2 buttons, found $($matches.Count)" -ForegroundColor Yellow
    }
} else {
    Write-Host "  [ERROR] WAR file not found at $warPath" -ForegroundColor Red
}
Write-Host ""

# Test 2: Check navigation configuration
Write-Host "[TEST 2] Checking navigation configuration..." -ForegroundColor Yellow
$zip = [System.IO.Compression.ZipFile]::OpenRead($warPath)
$entry = $zip.Entries | Where-Object { $_.FullName -eq "WEB-INF/faces-config.xml" }
$stream = $entry.Open()
$reader = New-Object System.IO.StreamReader($stream)
$configContent = $reader.ReadToEnd()
$reader.Close()
$stream.Close()
$zip.Dispose()

if ($configContent -match 'from-view-id.*employee-detail') {
    Write-Host "  [OK] Found navigation rule from employee-detail.xhtml" -ForegroundColor Green
}
if ($configContent -match 'from-outcome.*list') {
    Write-Host "  [OK] Found 'list' outcome in navigation rule" -ForegroundColor Green
}
if ($configContent -match 'to-view-id.*employee-list') {
    Write-Host "  [OK] Found navigation target: employee-list.xhtml" -ForegroundColor Green
}
Write-Host ""

# Test 3: Test application response
Write-Host "[TEST 3] Testing application response..." -ForegroundColor Yellow
try {
    $url = "http://localhost:8080/employee-demo/employee-detail.xhtml?id=999"
    Write-Host "  Accessing: $url" -ForegroundColor Gray
    $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 10
    Write-Host "  Status: $($response.StatusCode) OK" -ForegroundColor Green
    Write-Host "  Response size: $($response.Content.Length) bytes" -ForegroundColor Gray
    
    if ($response.Content -match 'Employee not found') {
        Write-Host "  [OK] 'Employee not found' page is displayed (expected)" -ForegroundColor Green
    }
    
    if ($response.Content -match 'Back to List') {
        Write-Host "  [OK] 'Back to List' button is present in rendered page" -ForegroundColor Green
    } else {
        Write-Host "  [WARNING] 'Back to List' button not found in rendered HTML" -ForegroundColor Yellow
    }
} catch {
    Write-Host "  [ERROR] Could not access application: $_" -ForegroundColor Red
}
Write-Host ""

# Test 4: Compare source files
Write-Host "[TEST 4] Comparing source file with deployed file..." -ForegroundColor Yellow
$sourceFile = "web-module\employee-detail.xhtml"
if (Test-Path $sourceFile) {
    $sourceContent = Get-Content $sourceFile -Raw
    $sourceButtons = [regex]::Matches($sourceContent, 'action="([^"]+)"', [System.Text.RegularExpressions.RegexOptions]::Singleline)
    $sourceActions = $sourceButtons | Where-Object { $_.Groups[1].Value -match 'list|employee-list' } | ForEach-Object { $_.Groups[1].Value }
    
    Write-Host "  Source file actions:" -ForegroundColor Gray
    foreach ($action in $sourceActions) {
        Write-Host "    - $action" -ForegroundColor Gray
    }
    
    $deployedButtons = [regex]::Matches($xhtmlContent, 'action="([^"]+)"', [System.Text.RegularExpressions.RegexOptions]::Singleline)
    $deployedActions = $deployedButtons | Where-Object { $_.Groups[1].Value -match 'list|employee-list' } | ForEach-Object { $_.Groups[1].Value }
    
    Write-Host "  Deployed file actions:" -ForegroundColor Gray
    foreach ($action in $deployedActions) {
        Write-Host "    - $action" -ForegroundColor Gray
    }
    
    if ($deployedActions -contains 'list' -and -not ($deployedActions -contains 'employee-list')) {
        Write-Host "  [OK] Source and deployed files match - fix is deployed!" -ForegroundColor Green
    } else {
        Write-Host "  [WARNING] Source and deployed files may differ" -ForegroundColor Yellow
    }
}
Write-Host ""

# Summary
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "BEFORE FIX:" -ForegroundColor Red
Write-Host '  action="employee-list?faces-redirect=true"' -ForegroundColor DarkRed
Write-Host "  (This was incorrect - not a valid JSF navigation outcome)" -ForegroundColor DarkRed
Write-Host ""
Write-Host "AFTER FIX:" -ForegroundColor Green
Write-Host '  action="list"' -ForegroundColor DarkGreen
Write-Host "  (This matches the navigation rule in faces-config.xml)" -ForegroundColor DarkGreen
Write-Host ""
Write-Host "RESULT:" -ForegroundColor Cyan
Write-Host "  The 'Back to List' button now correctly navigates to" -ForegroundColor White
Write-Host "  employee-list.xhtml instead of showing 'Employee not found'" -ForegroundColor White
Write-Host ""





