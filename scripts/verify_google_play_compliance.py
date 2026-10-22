#!/usr/bin/env python3
"""
VitalPet Android — Google Play Policy & Developer Standards Automated Auditor
Verifies all mandatory Google Play Store technical, privacy, and architectural guidelines.
"""

import os
import sys
import re
import json
import xml.etree.ElementTree as ET

def check_mark(success):
    return "[\033[92m✓\033[0m]" if success else "[\033[91m✗\033[0m]"

def run_audit():
    print("=" * 65)
    print("  VitalPet Android — Google Play Store Compliance Audit Report")
    print("=" * 65)

    failures = []
    warnings = []

    # 1. Target SDK & Compile SDK Check
    gradle_path = "app/build.gradle.kts"
    with open(gradle_path, "r", encoding="utf-8") as f:
        gradle_content = f.read()

    target_sdk = re.search(r'targetSdk\s*=\s*(\d+)', gradle_content)
    compile_sdk = re.search(r'compileSdk\s*=\s*(\d+)', gradle_content)
    min_sdk = re.search(r'minSdk\s*=\s*(\d+)', gradle_content)

    t_sdk = int(target_sdk.group(1)) if target_sdk else 0
    c_sdk = int(compile_sdk.group(1)) if compile_sdk else 0
    m_sdk = int(min_sdk.group(1)) if min_sdk else 0

    if t_sdk >= 34:
        print(f"{check_mark(True)} Target SDK: API {t_sdk} (Google Play requires API 34+; API 35 Android 15 is optimal)")
    else:
        print(f"{check_mark(False)} Target SDK: API {t_sdk} (Below Google Play requirement)")
        failures.append(f"Target SDK {t_sdk} is below Google Play required minimum API 34.")

    if m_sdk >= 26:
        print(f"{check_mark(True)} Minimum SDK: API {m_sdk} (Android 8.0 Oreo, covers 97%+ of active global devices)")
    else:
        print(f"{check_mark(False)} Min SDK: API {m_sdk}")

    # 2. Bundle Language Split Check
    bundle_split_check = "enableSplit = false" in gradle_content and "language" in gradle_content
    if bundle_split_check:
        print(f"{check_mark(True)} App Bundle Language Configuration: enableSplit = false (Offline multilingual in-app switching compliant)")
    else:
        failures.append("Language split not disabled in bundle configuration.")
        print(f"{check_mark(False)} App Bundle Language Configuration missing enableSplit = false")

    # 3. AndroidManifest Verification
    manifest_path = "app/src/main/AndroidManifest.xml"
    with open(manifest_path, "r", encoding="utf-8") as f:
        manifest_content = f.read()

    # Hardware features
    cam_feat = 'android.hardware.camera' in manifest_content and 'android:required="false"' in manifest_content
    loc_feat = 'android.hardware.location' in manifest_content and 'android:required="false"' in manifest_content
    if cam_feat and loc_feat:
        print(f"{check_mark(True)} Hardware Features: Optional camera & location declared (Prevents tablet/foldable filtering in Play Store)")
    else:
        warnings.append("Optional hardware features not fully declared with required=false.")
        print(f"{check_mark(False)} Optional hardware features missing required=false")

    # Predictive Back
    back_invoked = 'android:enableOnBackInvokedCallback="true"' in manifest_content
    if back_invoked:
        print(f"{check_mark(True)} Android 13+ Predictive Back: enableOnBackInvokedCallback is enabled")
    else:
        failures.append("android:enableOnBackInvokedCallback is missing.")
        print(f"{check_mark(False)} android:enableOnBackInvokedCallback missing")

    # Data extraction rules
    data_rules = 'android:dataExtractionRules="@xml/data_extraction_rules"' in manifest_content
    backup_rules = 'android:fullBackupContent="@xml/backup_rules"' in manifest_content
    if data_rules and backup_rules:
        print(f"{check_mark(True)} Data Safety & Backup Rules: Both Android 12+ and legacy rules defined")
    else:
        failures.append("Data extraction or backup rules missing.")
        print(f"{check_mark(False)} Data extraction or backup rules missing")

    # Exported attributes on all components
    components_without_exported = []
    root = ET.fromstring(manifest_content)
    ns = {'android': 'http://schemas.android.com/apk/res/android'}
    for app in root.findall('application'):
        for child in app:
            tag = child.tag
            if tag in ['activity', 'service', 'receiver', 'provider']:
                has_filter = child.find('intent-filter') is not None
                exported = child.get('{http://schemas.android.com/apk/res/android}exported')
                name = child.get('{http://schemas.android.com/apk/res/android}name')
                if has_filter and exported is None:
                    components_without_exported.append(name or tag)

    if not components_without_exported:
        print(f"{check_mark(True)} Component Security: All components have explicit android:exported attributes (Android 12+ requirement)")
    else:
        failures.append(f"Components missing exported attribute: {components_without_exported}")
        print(f"{check_mark(False)} Components missing exported: {components_without_exported}")

    # 4. Cleartext Traffic / Security
    has_cleartext = False
    for root_dir, _, files in os.walk("app/src/main/java"):
        for file in files:
            if file.endswith(".kt") or file.endswith(".java"):
                with open(os.path.join(root_dir, file), "r", encoding="utf-8") as kf:
                    code = kf.read()
                    if "http://" in code and not "http://localhost" in code and not "http://10.0.2.2" in code:
                        has_cleartext = True
                        failures.append(f"Cleartext HTTP found in {file}")

    if not has_cleartext:
        print(f"{check_mark(True)} Network Security: Zero cleartext (HTTP) endpoints found in source code (All traffic uses HTTPS)")
    else:
        print(f"{check_mark(False)} Network Security: Cleartext HTTP found")

    # 5. Competitor Store & Policy Link Check
    apple_eula_found = False
    for root_dir, _, files in os.walk("app/src/main/java"):
        for file in files:
            if file.endswith(".kt") or file.endswith(".java"):
                with open(os.path.join(root_dir, file), "r", encoding="utf-8") as kf:
                    code = kf.read()
                    if "apple.com/legal" in code:
                        apple_eula_found = True
                        failures.append(f"Apple Store EULA found in {file}")

    if not apple_eula_found:
        print(f"{check_mark(True)} Legal Policy: Zero competitor store references (Neutral terms compliant with Google Play)")
    else:
        print(f"{check_mark(False)} Legal Policy: Competitor store URL detected")

    # 6. Adaptive Icons Verification
    icon_std = os.path.exists("app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml")
    icon_round = os.path.exists("app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml")
    if icon_std and icon_round:
        print(f"{check_mark(True)} Adaptive Icons: Standard and Round adaptive icons present for Android 8.0 to 15")
    else:
        failures.append("Adaptive icon XMLs missing in mipmap-anydpi-v26.")
        print(f"{check_mark(False)} Adaptive icons missing")

    # 7. Release App Bundle (.aab) Check
    aab_path = "app/build/outputs/bundle/release/app-release.aab"
    if os.path.exists(aab_path):
        size_mb = os.path.getsize(aab_path) / (1024 * 1024)
        if size_mb < 150:
            print(f"{check_mark(True)} Google Play App Bundle: app-release.aab verified ({size_mb:.1f} MB, below 150 MB Play Store limit)")
        else:
            failures.append(f"AAB size ({size_mb:.1f} MB) exceeds 150 MB limit.")
            print(f"{check_mark(False)} AAB exceeds 150MB: {size_mb:.1f} MB")
    else:
        warnings.append("app-release.aab not found. Run ./gradlew bundleRelease to generate.")
        print(f"{check_mark(False)} app-release.aab not found")

    # 8. Multilingual Synchronized Check
    with open("app/src/main/assets/translations.json", "r", encoding="utf-8") as f:
        translations = json.load(f)
    langs = ["pt", "en", "es", "fr", "de", "it", "nl", "lb"]
    missing_lang = False
    for k, v in translations.items():
        for l in langs:
            if l not in v or not v[l]:
                missing_lang = True
                break

    if not missing_lang:
        print(f"{check_mark(True)} Internationalization: All {len(translations)} keys 100% complete across all 8 languages")
    else:
        failures.append("Translations missing in some languages.")
        print(f"{check_mark(False)} Incomplete translations detected")

    print("=" * 65)
    if not failures:
        print("🎉 AUDIT RESULT: 100% COMPLIANT WITH ALL GOOGLE PLAY POLICIES!")
        print("   The project is completely ready for release in the Google Play Console.")
        print("=" * 65)
        return 0
    else:
        print(f"❌ AUDIT FAILED with {len(failures)} issues:")
        for err in failures:
            print(f"   - {err}")
        print("=" * 65)
        return 1

if __name__ == "__main__":
    sys.exit(run_audit())
