@echo off
setlocal enabledelayedexpansion

echo ============================================================
echo   VitalPet - Compilacao e Empacotamento Windows Store (MSIX)
echo ============================================================

cd /d "%~dp0\..\.."

echo [*] A compilar distribuicao nativa VitalPet com Gradle...
call gradlew.bat :desktop:createDistributable
if %ERRORLEVEL% neq 0 (
    echo [!] Erro ao compilar com Gradle.
    pause
    exit /b %ERRORLEVEL%
)

echo [*] A gerar pacote MSIX / APPX da Store...
python scripts\package_windows_store.py
if %ERRORLEVEL% neq 0 (
    echo [!] Erro ao gerar pacote da store.
    pause
    exit /b %ERRORLEVEL%
)

echo ============================================================
echo [OK] Pacote VitalPet.msix pronto para a Microsoft Store!
echo Localizacao: desktop\build\windows-store\VitalPet.msix
echo ============================================================
pause
