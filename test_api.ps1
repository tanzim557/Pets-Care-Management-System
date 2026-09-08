$s = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginResponse = Invoke-RestMethod -Uri 'http://localhost:8081/login' -Method Post -Body 'username=admin&password=admin123' -ContentType 'application/x-www-form-urlencoded' -WebSession $s
$statsResponse = Invoke-RestMethod -Uri 'http://localhost:8081/api/admin/stats' -WebSession $s
$statsResponse | ConvertTo-Json
