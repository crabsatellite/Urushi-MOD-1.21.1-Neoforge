[CmdletBinding()]
param(
    [string]$JavaHome = 'C:\Program Files\Java\jdk-21',
    [string]$WorldName = 'New World',
    [int]$TimeoutSeconds = 180
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$ProjectRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$receiptPath = Join-Path $ProjectRoot 'build\minecraft-mod-testing\urushi-client-interaction-receipt.json'
$runtimeDirectory = Join-Path $ProjectRoot 'build\minecraft-mod-testing\client-interaction'
$invokeSession = Join-Path $ProjectRoot '.agents\skills\minecraft-mod-testing\scripts\invoke_single_session.ps1'

Remove-Item -LiteralPath $receiptPath -Force -ErrorAction SilentlyContinue

$oldJavaHome = $env:JAVA_HOME
$oldPath = $env:Path
$oldGate = $env:URUSHI_CLIENT_INTERACTION_GATE
$oldWorld = $env:URUSHI_QUICK_PLAY_WORLD
$oldReceipt = $env:URUSHI_CLIENT_TEST_RECEIPT

try {
    $env:JAVA_HOME = $JavaHome
    $env:Path = "$JavaHome\bin;$oldPath"
    $env:URUSHI_CLIENT_INTERACTION_GATE = 'true'
    $env:URUSHI_QUICK_PLAY_WORLD = $WorldName
    $env:URUSHI_CLIENT_TEST_RECEIPT = $receiptPath

    if (Test-Path -LiteralPath $invokeSession -PathType Leaf) {
        $gradleArguments = @(
            "-PminecraftModTestingRuntimeDirectory=$runtimeDirectory",
            'runClient',
            '--no-daemon'
        )
        & $invokeSession `
            -ProjectRoot $ProjectRoot `
            -Executable (Join-Path $ProjectRoot 'gradlew.bat') `
            -ArgumentList $gradleArguments `
            -RuntimeKind client `
            -HarnessReceiptPath 'build/minecraft-mod-testing/urushi-client-interaction-receipt.json' `
            -ExpectedScenarioCount 4 `
            -ExpectedProcessStartCount 1 `
            -ExpectedWorldLoadCount 1 `
            -RuntimeDirectory $runtimeDirectory
    } else {
        & (Join-Path $ProjectRoot 'gradlew.bat') runClient --no-daemon
    }

    if ($LASTEXITCODE -ne 0) {
        throw "runClient exited with code $LASTEXITCODE"
    }
    if (-not (Test-Path -LiteralPath $receiptPath -PathType Leaf)) {
        throw "client harness receipt was not written: $receiptPath"
    }

    $receipt = Get-Content -Raw -LiteralPath $receiptPath | ConvertFrom-Json
    if (-not [bool]$receipt.allScenariosPassed) {
        throw "client interaction gate reported failure: $($receipt.failure)"
    }
    Write-Output 'URUSHI_CLIENT_INTERACTION_GATE_OK'
    Write-Output (Get-Content -Raw -LiteralPath $receiptPath)
    exit 0
} catch {
    Write-Output 'URUSHI_CLIENT_INTERACTION_GATE_FAILED'
    Write-Output $_.Exception.Message
    if (Test-Path -LiteralPath $receiptPath -PathType Leaf) {
        Get-Content -Raw -LiteralPath $receiptPath
    }
    exit 1
} finally {
    $env:JAVA_HOME = $oldJavaHome
    $env:Path = $oldPath
    $env:URUSHI_CLIENT_INTERACTION_GATE = $oldGate
    $env:URUSHI_QUICK_PLAY_WORLD = $oldWorld
    $env:URUSHI_CLIENT_TEST_RECEIPT = $oldReceipt
}
