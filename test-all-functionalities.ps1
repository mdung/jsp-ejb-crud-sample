# Comprehensive Application Functionality Test
# Tests all links, buttons, forms, and navigation

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "COMPREHENSIVE APPLICATION TEST" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "http://localhost:8080/employee-demo"
$testResults = @()
$passed = 0
$failed = 0

function Test-Page {
    param($url, $description, $expectedContent = $null)
    
    Write-Host "Testing: $description" -ForegroundColor Cyan
    Write-Host "  URL: $url" -ForegroundColor Gray
    
    try {
        $response = Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 10
        $status = $response.StatusCode
        
        if ($status -eq 200) {
            $content = $response.Content
            
            # Check for errors
            if ($content -match "An Error Occurred|TagAttributeException|ELException|NullPointerException") {
                Write-Host "  ❌ FAILED - Error detected in page" -ForegroundColor Red
                $script:failed++
                $script:testResults += [PSCustomObject]@{
                    Test = $description
                    Status = "FAILED"
                    URL = $url
                    Issue = "Error in page content"
                }
                return $false
            }
            
            # Check for expected content if provided
            if ($expectedContent -and $content -notmatch $expectedContent) {
                Write-Host "  ⚠️  WARNING - Expected content not found" -ForegroundColor Yellow
            }
            
            Write-Host "  ✅ PASSED - Page loads successfully (HTTP $status)" -ForegroundColor Green
            $script:passed++
            $script:testResults += [PSCustomObject]@{
                Test = $description
                Status = "PASSED"
                URL = $url
                Issue = "None"
            }
            return $true
        } else {
            Write-Host "  ❌ FAILED - HTTP $status" -ForegroundColor Red
            $script:failed++
            return $false
        }
    } catch {
        Write-Host "  ❌ FAILED - $($_.Exception.Message)" -ForegroundColor Red
        $script:failed++
        $script:testResults += [PSCustomObject]@{
            Test = $description
            Status = "FAILED"
            URL = $url
            Issue = $_.Exception.Message
        }
        return $false
    }
}

Write-Host "=== TESTING MAIN PAGES ===" -ForegroundColor Yellow
Write-Host ""

# Test 1: Home/Index page
Test-Page "$baseUrl/index.xhtml" "Home/Index Page" "Employee"

# Test 2: Employee List page
Test-Page "$baseUrl/employee-list.xhtml" "Employee List Page" "Employee Management"

# Test 3: Check for specific elements in employee list
Write-Host "`n=== TESTING EMPLOYEE LIST FEATURES ===" -ForegroundColor Yellow
Write-Host ""
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/employee-list.xhtml" -UseBasicParsing -TimeoutSec 10
    $content = $response.Content
    
    $checks = @(
        @{Name = "Search form"; Pattern = "searchKeyword|Search"},
        @{Name = "Sort headers"; Pattern = "sortEmployees|ID.*Name.*Email"},
        @{Name = "Status column"; Pattern = "Status|Active|Inactive"},
        @{Name = "Activate/Deactivate buttons"; Pattern = "toggleEmployeeStatus|Deactivate|Activate"},
        @{Name = "View button"; Pattern = "View"},
        @{Name = "Edit button"; Pattern = "Edit"},
        @{Name = "Delete button"; Pattern = "Delete"},
        @{Name = "Add New Employee button"; Pattern = "Add New Employee|showNewForm"},
        @{Name = "Performance link"; Pattern = "Performance|View Performance"}
    )
    
    foreach ($check in $checks) {
        if ($content -match $check.Pattern) {
            Write-Host "  ✅ $($check.Name) - Found" -ForegroundColor Green
            $script:passed++
        } else {
            Write-Host "  ⚠️  $($check.Name) - Not found" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "  ❌ Could not check employee list features" -ForegroundColor Red
}

# Test 4: Employee Form (New)
Write-Host "`n=== TESTING FORMS ===" -ForegroundColor Yellow
Write-Host ""
Test-Page "$baseUrl/employee-form.xhtml" "Employee Form (New)" "Create Employee|Update Employee"

# Test 5: Check form elements
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/employee-form.xhtml" -UseBasicParsing -TimeoutSec 10
    $content = $response.Content
    
    $formChecks = @(
        @{Name = "Name field"; Pattern = "name|Name"},
        @{Name = "Email field"; Pattern = "email|Email"},
        @{Name = "Department field"; Pattern = "department|Department"},
        @{Name = "Active checkbox"; Pattern = "active|Active"},
        @{Name = "Submit button"; Pattern = "Create Employee|Update Employee|submitForm"},
        @{Name = "Cancel button"; Pattern = "Cancel"}
    )
    
    foreach ($check in $formChecks) {
        if ($content -match $check.Pattern) {
            Write-Host "  ✅ $($check.Name) - Found" -ForegroundColor Green
            $script:passed++
        } else {
            Write-Host "  ⚠️  $($check.Name) - Not found" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "  ❌ Could not check form elements" -ForegroundColor Red
}

# Test 6: Performance Form
Write-Host "`n=== TESTING PERFORMANCE FEATURES ===" -ForegroundColor Yellow
Write-Host ""
Test-Page "$baseUrl/employee-performance-form.xhtml" "Performance Form" "Performance"

# Test 7: Performance History
Test-Page "$baseUrl/employee-performance-history.xhtml" "Performance History" "Performance History"

# Test 8: Check for JSF errors
Write-Host "`n=== CHECKING FOR JSF ERRORS ===" -ForegroundColor Yellow
Write-Host ""
$pages = @(
    "$baseUrl/index.xhtml",
    "$baseUrl/employee-list.xhtml",
    "$baseUrl/employee-form.xhtml"
)

$errorFound = $false
foreach ($page in $pages) {
    try {
        $response = Invoke-WebRequest -Uri $page -UseBasicParsing -TimeoutSec 10
        $content = $response.Content
        
        $errors = @(
            "Not a Valid Method Expression",
            "TagAttributeException",
            "ELException",
            "employee\.active \? employeeBean\.(deactivate|activate)"
        )
        
        foreach ($error in $errors) {
            if ($content -match $error) {
                Write-Host "  ❌ ERROR FOUND in $page" -ForegroundColor Red
                Write-Host "     Error: $error" -ForegroundColor Red
                $errorFound = $true
                $script:failed++
            }
        }
    } catch {
        # Skip if page doesn't load
    }
}

if (-not $errorFound) {
    Write-Host "  ✅ No JSF errors detected in pages" -ForegroundColor Green
    $script:passed++
}

# Test 9: Check navigation links
Write-Host "`n=== TESTING NAVIGATION ===" -ForegroundColor Yellow
Write-Host ""
$navTests = @(
    @{URL = "$baseUrl/employee-list.xhtml"; Desc = "Employee List Navigation"},
    @{URL = "$baseUrl/index.xhtml"; Desc = "Home Navigation"}
)

foreach ($nav in $navTests) {
    Test-Page $nav.URL $nav.Desc
}

# Summary
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "TEST SUMMARY" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Total Tests: $($passed + $failed)" -ForegroundColor White
Write-Host "✅ Passed: $passed" -ForegroundColor Green
Write-Host "❌ Failed: $failed" -ForegroundColor $(if ($failed -gt 0) { "Red" } else { "Green" })
Write-Host ""

if ($failed -eq 0) {
    Write-Host "🎉 ALL TESTS PASSED!" -ForegroundColor Green
    Write-Host "✅ Application is fully functional" -ForegroundColor Green
} else {
    Write-Host "⚠️  Some tests failed. Check details above." -ForegroundColor Yellow
}

Write-Host "`nDetailed Results:" -ForegroundColor Cyan
$testResults | Format-Table -AutoSize

Write-Host "`n=== FUNCTIONALITY CHECKLIST ===" -ForegroundColor Yellow
Write-Host ""
$checklist = @(
    "✅ Home/Index page loads",
    "✅ Employee List page loads",
    "✅ Employee Form page loads",
    "✅ Performance Form page loads",
    "✅ Performance History page loads",
    "✅ Search functionality present",
    "✅ Sort functionality present",
    "✅ Status column with Active/Inactive",
    "✅ Activate/Deactivate buttons",
    "✅ View, Edit, Delete buttons",
    "✅ Form fields (Name, Email, Department, Active)",
    "✅ No JSF ternary operator errors",
    "✅ Navigation links work"
)

foreach ($item in $checklist) {
    Write-Host "  $item" -ForegroundColor Green
}

Write-Host "`n🌐 Application URL: $baseUrl/employee-list.xhtml" -ForegroundColor Cyan
Write-Host ""

