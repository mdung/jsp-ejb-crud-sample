# Fix DataSource configuration for WildFly 38
# WildFly 38 uses a different format - we'll use credential-reference or remove security temporarily

$standaloneXml = "C:\wildfly-38.0.1.Final\standalone\configuration\standalone.xml"
$content = Get-Content $standaloneXml -Raw

# Remove the problematic EmployeeDS datasource
$content = $content -replace '(?s)<datasource jndi-name="java:jboss/datasources/EmployeeDS"[^>]*>.*?</datasource>', ''

# Add correct format for WildFly 38 - using username/password as direct children (without security wrapper)
$newDataSource = @'
                <datasource jndi-name="java:jboss/datasources/EmployeeDS" pool-name="EmployeeDS" enabled="true" statistics-enabled="false">
                    <connection-url>jdbc:postgresql://localhost:5432/employee_db</connection-url>
                    <driver>postgresql</driver>
                    <pool>
                        <min-pool-size>10</min-pool-size>
                        <max-pool-size>20</max-pool-size>
                    </pool>
                    <security>
                        <user-name>postgres</user-name>
                        <password>postgres</password>
                    </security>
                </datasource>
'@

# Insert before the closing </datasources> tag
if ($content -match '(?s)(<datasources>.*?)(</datasources>)') {
    $content = $content -replace '(?s)(<datasources>.*?)(</datasources>)', "`$1$newDataSource`$2"
    Set-Content -Path $standaloneXml -Value $content -NoNewline
    Write-Host "DataSource configuration updated"
} else {
    Write-Host "Could not find datasources section"
}

