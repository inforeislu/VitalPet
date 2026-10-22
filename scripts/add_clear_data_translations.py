#!/usr/bin/env python3
import json
import re

clear_data_translations = {
    "Eliminar Todos os Dados": {
        "pt": "Eliminar Todos os Dados",
        "en": "Clear All Data",
        "es": "Eliminar Todos los Datos",
        "fr": "Supprimer Toutes les Données",
        "de": "Alle Daten löschen",
        "it": "Elimina Tutti i Dati",
        "nl": "Alle Gegevens Wisgen",
        "lb": "All Daten läschen"
    },
    "Limpar a aplicação e remover todos os animais registados": {
        "pt": "Limpar a aplicação e remover todos os animais registados",
        "en": "Reset application and remove all registered pets",
        "es": "Reiniciar aplicación y eliminar todas las mascotas registradas",
        "fr": "Réinitialiser l'application et supprimer tous les animaux enregistrés",
        "de": "App zurücksetzen und alle registrierten Haustiere entfernen",
        "it": "Reimposta app ed elimina tutti gli animali registrati",
        "nl": "Reset de applicatie en verwijder alle geregistreerde huisdieren",
        "lb": "App zrécksetzen an all registréiert Hausdéieren läschen"
    },
    "Limpar Aplicação": {
        "pt": "Limpar Aplicação",
        "en": "Reset Application",
        "es": "Reiniciar Aplicación",
        "fr": "Réinitialiser l'Application",
        "de": "App zurücksetzen",
        "it": "Reimposta Applicazione",
        "nl": "Applicatie Resetten",
        "lb": "App zrécksetzen"
    },
    "Tem a certeza que deseja eliminar todos os animais e registos? Esta ação é irreversível.": {
        "pt": "Tem a certeza que deseja eliminar todos os animais e registos? Esta ação é irreversível.",
        "en": "Are you sure you want to delete all pets and records? This action cannot be undone.",
        "es": "¿Está seguro de que desea eliminar todas las mascotas y registros? Esta acción es irreversible.",
        "fr": "Êtes-vous sûr de vouloir supprimer tous les animaux et dossiers? Cette action est irréversible.",
        "de": "Sind Sie sicher, dass Sie alle Haustiere und Einträge löschen möchten? Dies kann nicht rückgängig gemacht werden.",
        "it": "Sei sicuro di voler eliminare tutti gli animali e i dati? Questa azione è irreversibile.",
        "nl": "Weet u zeker dat u alle huisdieren en gegevens wilt verwijderen? Dit kan niet ongedaan worden gemaakt.",
        "lb": "Sidd Dir sicher datt Dir all Hausdéieren an Eintrags läschen wëllt? Dëst kann net zréckgesat ginn."
    },
    "Todos os dados foram eliminados": {
        "pt": "Todos os dados foram eliminados",
        "en": "All data has been deleted",
        "es": "Todos los datos han sido eliminados",
        "fr": "Toutes les données ont été supprimées",
        "de": "Alle Daten wurden gelöscht",
        "it": "Tutti i dati sono stati eliminati",
        "nl": "Alle gegevens zijn verwijderd",
        "lb": "All Daten goufen geläscht"
    },
    "Eliminar Tudo": {
        "pt": "Eliminar Tudo",
        "en": "Delete All",
        "es": "Eliminar Todo",
        "fr": "Tout Supprimer",
        "de": "Alles löschen",
        "it": "Elimina Tutto",
        "nl": "Alles Verwijderen",
        "lb": "Alles läschen"
    }
}

with open("app/src/main/assets/translations.json", "r", encoding="utf-8") as f:
    translations = json.load(f)

for k, v in clear_data_translations.items():
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
    for k, trans in clear_data_translations.items():
        xml_key = clean_key_name(k)
        if not xml_key:
            continue
        if f'name="{xml_key}"' not in content:
            val = trans[lang].replace("&", "&amp;").replace("'", "\\'").replace('"', '\\"')
            new_entries.append(f'    <string name="{xml_key}">{val}</string>')

    if new_entries:
        insert_idx = content.rfind("</resources>")
        if insert_idx != -1:
            updated_content = content[:insert_idx] + "\n".join(new_entries) + "\n" + content[insert_idx:]
            with open(filepath, "w", encoding="utf-8") as f:
                f.write(updated_content)

print("Successfully added clear data translations across all 8 languages.")
