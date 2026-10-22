#!/usr/bin/env python3
"""
VitalPet Language Validator
Enforces mandatory 8-language support across the Android project.
"""

import json
import os
import sys
import xml.etree.ElementTree as ET

LANGUAGES = ["pt", "en", "es", "fr", "de", "it", "nl", "lb"]
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TRANSLATIONS_JSON = os.path.join(BASE_DIR, "app/src/main/assets/translations.json")
RES_DIR = os.path.join(BASE_DIR, "app/src/main/res")

RES_FOLDER_MAP = {
    "pt": "values",
    "en": "values-en",
    "es": "values-es",
    "fr": "values-fr",
    "de": "values-de",
    "it": "values-it",
    "nl": "values-nl",
    "lb": "values-lb"
}

def validate_json():
    print(f"[*] Validating {TRANSLATIONS_JSON}...")
    if not os.path.exists(TRANSLATIONS_JSON):
        print(f"[!] Error: {TRANSLATIONS_JSON} does not exist!")
        return False
    
    with open(TRANSLATIONS_JSON, "r", encoding="utf-8") as f:
        data = json.load(f)
    
    total_keys = len(data)
    print(f"    Found {total_keys} keys in translations.json.")
    
    errors = []
    for key, trans in data.items():
        for lang in LANGUAGES:
            if lang not in trans:
                errors.append(f"Key '{key}' is missing translation for '{lang}'.")
            elif not trans[lang] or not trans[lang].strip():
                errors.append(f"Key '{key}' has empty translation for '{lang}'.")
    
    if errors:
        print(f"[!] Found {len(errors)} issues in translations.json:")
        for err in errors[:10]:
            print("   -", err)
        if len(errors) > 10:
            print(f"   ... and {len(errors) - 10} more.")
        return False
    
    print("[✓] translations.json is 100% compliant with all 8 languages.")
    return True

def validate_xml():
    print(f"[*] Validating XML resource files in {RES_DIR}...")
    base_file = os.path.join(RES_DIR, "values", "strings.xml")
    if not os.path.exists(base_file):
        print(f"[!] Error: Base strings.xml not found at {base_file}")
        return False
    
    tree = ET.parse(base_file)
    base_keys = set(elem.attrib.get("name") for elem in tree.getroot().findall("string"))
    print(f"    Base language (pt) contains {len(base_keys)} string resources.")
    
    all_ok = True
    for lang, folder in RES_FOLDER_MAP.items():
        if lang == "pt":
            continue
        file_path = os.path.join(RES_DIR, folder, "strings.xml")
        if not os.path.exists(file_path):
            print(f"[!] Missing strings.xml for language '{lang}' ({file_path})")
            all_ok = False
            continue
        lang_tree = ET.parse(file_path)
        lang_keys = set(elem.attrib.get("name") for elem in lang_tree.getroot().findall("string"))
        missing = base_keys - lang_keys
        if missing:
            print(f"[!] Language '{lang}' is missing {len(missing)} keys from base strings.xml:")
            for m in list(missing)[:5]:
                print(f"    - {m}")
            all_ok = False
        else:
            print(f"    [✓] Language '{lang}' ({folder}) is synchronized ({len(lang_keys)} strings).")
    
    return all_ok

def main():
    print("=" * 60)
    print("  VitalPet Android — Language Integrity & Compliance Check")
    print("  Required Languages:", ", ".join(LANGUAGES))
    print("=" * 60)
    
    ok_json = validate_json()
    ok_xml = validate_xml()
    
    print("=" * 60)
    if ok_json and ok_xml:
        print("🎉 SUCCESS: All 8 languages are 100% complete and synchronized!")
        sys.exit(0)
    else:
        print("❌ FAILURE: Language synchronization check failed.")
        sys.exit(1)

if __name__ == "__main__":
    main()
