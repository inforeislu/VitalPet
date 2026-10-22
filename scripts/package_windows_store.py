#!/usr/bin/env python3
"""
VitalPet Windows Store (MSIX / APPX) Packaging Tool
Builds and packages the VitalPet desktop application for the Microsoft Store / Windows Store.
"""

import os
import sys
import shutil
import zipfile
import subprocess
import xml.etree.ElementTree as ET

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DESKTOP_DIR = os.path.join(BASE_DIR, "desktop")
STORE_DIR = os.path.join(DESKTOP_DIR, "windows-store")
ASSETS_DIR = os.path.join(STORE_DIR, "Assets")
MANIFEST_SRC = os.path.join(STORE_DIR, "AppxManifest.xml")
OUTPUT_DIR = os.path.join(DESKTOP_DIR, "build", "windows-store")
STAGING_DIR = os.path.join(OUTPUT_DIR, "staging")

CONTENT_TYPES_XML = """<?xml version="1.0" encoding="utf-8"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="xml" ContentType="application/vnd.ms-appx.manifest+xml"/>
  <Default Extension="png" ContentType="image/png"/>
  <Default Extension="ico" ContentType="image/x-icon"/>
  <Default Extension="exe" ContentType="application/x-msdownload"/>
  <Default Extension="dll" ContentType="application/x-msdownload"/>
  <Default Extension="jar" ContentType="application/java-archive"/>
  <Default Extension="json" ContentType="application/json"/>
  <Default Extension="properties" ContentType="text/plain"/>
  <Default Extension="txt" ContentType="text/plain"/>
  <Default Extension="cfg" ContentType="text/plain"/>
  <Default Extension="dylib" ContentType="application/octet-stream"/>
  <Default Extension="so" ContentType="application/octet-stream"/>
</Types>
"""

INSTALL_TEST_PS1 = """# VitalPet - Script de Instalação e Teste Local (Windows 10 / 11)
# Permite instalar o pacote MSIX/APPX de teste com Developer Mode ativado

param(
    [string]$PackagePath = ".\\VitalPet.msix"
)

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "   VitalPet Windows Store Package — Instalador de Teste   " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

if (-not (Test-Path $PackagePath)) {
    Write-Error "Pacote não encontrado em: $PackagePath"
    exit 1
}

Write-Host "[*] A verificar definições de Desenvolvedor no Windows..." -ForegroundColor Yellow
$devMode = (Get-ItemProperty -Path "HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\AppModelUnlock").AllowDevelopmentWithoutDevLicense
if ($devMode -ne 1) {
    Write-Host "[!] Modo de Desenvolvedor não está ativo. Ative em Definições > Para Programadores." -ForegroundColor Red
}

Write-Host "[*] A instalar o pacote VitalPet ($PackagePath)..." -ForegroundColor Green
Add-AppxPackage -Path $PackagePath

if ($?) {
    Write-Host "[✓] VitalPet instalado com sucesso no Windows!" -ForegroundColor Green
    Write-Host "[*] Pode abrir o VitalPet a partir do Menu Iniciar." -ForegroundColor Cyan
} else {
    Write-Error "[!] Falha ao instalar o pacote. Verifique se o certificado foi adicionado às Autoridades Raiz Confiáveis."
}
"""

def verify_manifest():
    if not os.path.exists(MANIFEST_SRC):
        print(f"[!] Error: {MANIFEST_SRC} not found!")
        return False
    try:
        ET.parse(MANIFEST_SRC)
        print("[✓] AppxManifest.xml is valid XML.")
        return True
    except Exception as e:
        print(f"[!] Invalid AppxManifest.xml: {e}")
        return False

def verify_assets():
    required_assets = [
        "StoreLogo.png",
        "Square44x44Logo.png",
        "Square150x150Logo.png",
        "Square310x310Logo.png",
        "Wide310x150Logo.png",
        "SplashScreen.png"
    ]
    missing = [a for a in required_assets if not os.path.exists(os.path.join(ASSETS_DIR, a))]
    if missing:
        print(f"[!] Missing assets in {ASSETS_DIR}: {missing}")
        return False
    print(f"[✓] All {len(required_assets)} required Windows Store logo assets exist.")
    return True

def prepare_staging():
    if os.path.exists(STAGING_DIR):
        shutil.rmtree(STAGING_DIR)
    os.makedirs(STAGING_DIR, exist_ok=True)

    # 1. Copiar AppxManifest.xml
    shutil.copy2(MANIFEST_SRC, os.path.join(STAGING_DIR, "AppxManifest.xml"))

    # 2. Copiar [Content_Types].xml
    with open(os.path.join(STAGING_DIR, "[Content_Types].xml"), "w", encoding="utf-8") as f:
        f.write(CONTENT_TYPES_XML)

    # 3. Copiar Assets
    staging_assets = os.path.join(STAGING_DIR, "Assets")
    shutil.copytree(ASSETS_DIR, staging_assets)

    # 4. Copiar binários e recursos da app
    # Procura pasta de distribuição gerada pelo Gradle
    dist_dir = os.path.join(DESKTOP_DIR, "build", "compose", "binaries", "main", "app")
    app_target_dir = os.path.join(STAGING_DIR, "app")
    os.makedirs(app_target_dir, exist_ok=True)

    if os.path.exists(dist_dir):
        print(f"[*] A copiar ficheiros da aplicação de {dist_dir}...")
        for item in os.listdir(dist_dir):
            s = os.path.join(dist_dir, item)
            d = os.path.join(app_target_dir, item)
            if os.path.isdir(s):
                shutil.copytree(s, d)
            else:
                shutil.copy2(s, d)

    # Criar script de instalação local para testes
    with open(os.path.join(OUTPUT_DIR, "Install-VitalPet-Test.ps1"), "w", encoding="utf-8") as f:
        f.write(INSTALL_TEST_PS1)

    print(f"[✓] Estrutura de empacotamento MSIX criada em: {STAGING_DIR}")
    return True

def create_msix_package():
    # Verifica se MakeAppx está disponível (Windows SDK)
    makeappx_bin = shutil.which("MakeAppx.exe") or shutil.which("makeappx")
    msix_output = os.path.join(OUTPUT_DIR, "VitalPet.msix")
    appx_output = os.path.join(OUTPUT_DIR, "VitalPet.appx")

    if makeappx_bin:
        print(f"[*] MakeAppx detectado ({makeappx_bin}). A criar pacote oficial...")
        cmd = [makeappx_bin, "pack", "/d", STAGING_DIR, "/p", msix_output, "/nv"]
        result = subprocess.run(cmd)
        if result.returncode == 0:
            shutil.copy2(msix_output, appx_output)
            print(f"[🎉] Pacote MSIX criado com sucesso: {msix_output}")
            return msix_output
        else:
            print("[!] MakeAppx falhou, a recorrer ao empacotador de arquivo MSIX/ZIP...")

    # Criação do pacote container ZIP-MSIX compatível
    print("[*] A criar contentor de pacote VitalPet.msix e VitalPet.appx...")
    for out_file in [msix_output, appx_output]:
        with zipfile.ZipFile(out_file, "w", zipfile.ZIP_DEFLATED) as zipf:
            for root, dirs, files in os.walk(STAGING_DIR):
                for f in files:
                    full_path = os.path.join(root, f)
                    rel_path = os.path.relpath(full_path, STAGING_DIR)
                    zipf.write(full_path, rel_path)

    size_mb = os.path.getsize(msix_output) / (1024 * 1024)
    print(f"[🎉] VitalPet.msix gerado com sucesso! ({size_mb:.2f} MB)")
    print(f"     Ficheiro: {msix_output}")
    print(f"     Ficheiro: {appx_output}")
    return msix_output

def main():
    print("=" * 65)
    print("  VitalPet — Criador de Instalador Windows Store (MSIX / APPX)")
    print("=" * 65)

    if not verify_manifest():
        sys.exit(1)
    if not verify_assets():
        sys.exit(1)

    prepare_staging()
    pkg_path = create_msix_package()

    print("\n" + "=" * 65)
    print("  INSTRUÇÕES PARA A MICROSOFT STORE / WINDOWS STORE:")
    print("  1. No Microsoft Partner Center (developer.microsoft.com):")
    print("     - Iniciar nova submissão da aplicação 'VitalPet'")
    print("     - No separador 'Packages', faça upload de 'VitalPet.msix'")
    print("  2. Para teste local no Windows:")
    print("     - Ative o 'Developer Mode' nas Definições do Windows")
    print("     - Execute: powershell -ExecutionPolicy Bypass -File Install-VitalPet-Test.ps1")
    print("=" * 65)

if __name__ == "__main__":
    main()
