import json
import xml.etree.ElementTree as ET
import re

new_translations = {
    "Veterinários & Urgências": {
        "pt": "Veterinários & Urgências",
        "en": "Vets & Emergencies",
        "es": "Veterinarios y Urgencias",
        "fr": "Vétérinaires & Urgences",
        "de": "Tierärzte & Notfälle",
        "it": "Veterinari & Urgenze",
        "nl": "Dierenartsen & Noodgevallen",
        "lb": "Déierendokteren & Noutfäll"
    },
    "Primeiros Socorros": {
        "pt": "Primeiros Socorros",
        "en": "First Aid",
        "es": "Primeros Auxilios",
        "fr": "Premiers Secours",
        "de": "Erste Hilfe",
        "it": "Primo Soccorso",
        "nl": "Eerste Hulp",
        "lb": "Éischt Hëllef"
    },
    "Protocolos de Primeiros Socorros": {
        "pt": "Protocolos de Primeiros Socorros",
        "en": "First Aid Protocols",
        "es": "Protocolos de Primeros Auxilios",
        "fr": "Protocoles de Premiers Secours",
        "de": "Erste-Hilfe-Protokolle",
        "it": "Protocolli di Primo Soccorso",
        "nl": "Eerstehulpprotocollen",
        "lb": "Éischt Hëllef Protokoller"
    }
}

# Update translations.json
with open("app/src/main/assets/translations.json", "r", encoding="utf-8") as f:
    translations = json.load(f)

for k, v in new_translations.items():
    translations[k] = v

with open("app/src/main/assets/translations.json", "w", encoding="utf-8") as f:
    json.dump(translations, f, ensure_ascii=False, indent=2)

def clean_key_name(text):
    clean = re.sub(r'[^a-zA-Z0-9_]', '_', text.lower())
    clean = re.sub(r'_+', '_', clean).strip('_')
    if clean and clean[0].isdigit():
        clean = "k_" + clean
    return clean

lang_files = {
    "pt": "app/src/main/res/values/strings.xml",
    "en": "app/src/main/res/values-en/strings.xml",
    "es": "app/src/main/res/values-es/strings.xml",
    "fr": "app/src/main/res/values-fr/strings.xml",
    "de": "app/src/main/res/values-de/strings.xml",
    "it": "app/src/main/res/values-it/strings.xml",
    "nl": "app/src/main/res/values-nl/strings.xml",
    "lb": "app/src/main/res/values-lb/strings.xml"
}

for lang, filepath in lang_files.items():
    with open(filepath, "r", encoding="utf-8") as f:
        content = f.read()
    
    new_entries = []
    for k, trans in new_translations.items():
        xml_key = clean_key_name(k)
        if f'name="{xml_key}"' not in content:
            val = trans[lang].replace("&", "&amp;").replace("'", "\\'").replace('"', '\\"')
            new_entries.append(f'    <string name="{xml_key}">{val}</string>')
    
    if new_entries:
        insert_idx = content.rfind("</resources>")
        if insert_idx != -1:
            updated_content = content[:insert_idx] + "\n".join(new_entries) + "\n" + content[insert_idx:]
            with open(filepath, "w", encoding="utf-8") as f:
                f.write(updated_content)

print("Updated translations and XMLs")
