import json
import re

extra_translations = {
    "Todos os registos foram inspecionados com sucesso:": {
        "pt": "Todos os registos foram inspecionados com sucesso:",
        "en": "All records were inspected successfully:",
        "es": "Todos los registros fueron inspeccionados con éxito:",
        "fr": "Tous les dossiers ont été inspectés avec succès :",
        "de": "Alle Datensätze wurden erfolgreich überprüft:",
        "it": "Tutte le cartelle sono state ispezionate con successo:",
        "nl": "Alle dossiers zijn succesvol gecontroleerd:",
        "lb": "All Dossieren goufe mat Erfolleg iwwerpréift:"
    },
    "Registos de Peso:": {
        "pt": "Registos de Peso:",
        "en": "Weight Logs:",
        "es": "Registros de Peso:",
        "fr": "Suivis de Poids :",
        "de": "Gewichtsprotokolle:",
        "it": "Registri del Peso:",
        "nl": "Gewichtsdossiers:",
        "lb": "Gewiichtsprotokoller:"
    },
    "Vacinas & Desparasitações:": {
        "pt": "Vacinas & Desparasitações:",
        "en": "Vaccines & Deworming:",
        "es": "Vacunas y Desparasitaciones:",
        "fr": "Vaccins & Vermifuges :",
        "de": "Impfungen & Entwurmungen:",
        "it": "Vaccini & Sverminazioni:",
        "nl": "Vaccins & Ontworming:",
        "lb": "Impfunge & Entwurmungen:"
    },
    "Consultas & Sintomas:": {
        "pt": "Consultas & Sintomas:",
        "en": "Visits & Symptoms:",
        "es": "Consultas y Síntomas:",
        "fr": "Consultations & Symptômes :",
        "de": "Besuche & Symptome:",
        "it": "Visite & Sintomi:",
        "nl": "Consulten & Symptomen:",
        "lb": "Visitten & Symptomer:"
    },
    "Passeios & Jejum:": {
        "pt": "Passeios & Jejum:",
        "en": "Walks & Fasting:",
        "es": "Paseos y Ayuno:",
        "fr": "Promenades & Jeûne :",
        "de": "Spaziergänge & Fasten:",
        "it": "Passeggiate & Digiuno:",
        "nl": "Wandelingen & Vasten:",
        "lb": "Spadséiergäng & Faaschten:"
    },
    "Tamanho Total:": {
        "pt": "Tamanho Total:",
        "en": "Total Size:",
        "es": "Tamaño Total:",
        "fr": "Taille Totale :",
        "de": "Gesamtgröße:",
        "it": "Dimensione Totale:",
        "nl": "Totale Grootte:",
        "lb": "Total Gréisst:"
    },
    "Diagnóstico completo de integridade e consistência": {
        "pt": "Diagnóstico completo de integridade e consistência",
        "en": "Full integrity and consistency diagnostics",
        "es": "Diagnóstico completo de integridad y consistencia",
        "fr": "Diagnostic complet d'intégrité et de cohérence",
        "de": "Vollständige Integritäts- und Konsistenzdiagnose",
        "it": "Diagnosi completa di integrità e coerenza",
        "nl": "Volledige integriteits- en consistentiediagnose",
        "lb": "Komplett Integritéits- a Konsistenzdiagnos"
    },
    "Restaurar animais e registos de um ficheiro": {
        "pt": "Restaurar animais e registos de um ficheiro",
        "en": "Restore pets and records from a file",
        "es": "Restaurar mascotas y registros de un archivo",
        "fr": "Restaurer animaux et dossiers depuis un fichier",
        "de": "Tiere und Aufzeichnungen aus Datei wiederherstellen",
        "it": "Ripristina animali e registri da un file",
        "nl": "Herstel dieren en dossiers uit een bestand",
        "lb": "Déieren an Dossieren aus enger Datei restauréieren"
    }
}

# 1. Update translations.json
with open("app/src/main/assets/translations.json", "r", encoding="utf-8") as f:
    translations = json.load(f)

for k, v in extra_translations.items():
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
    for k, trans in extra_translations.items():
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

print("Updated extra dialog translations successfully")
