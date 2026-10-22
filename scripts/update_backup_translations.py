import json
import re

new_translations = {
    "Verificação de Integridade dos Dados": {
        "pt": "Verificação de Integridade dos Dados",
        "en": "Data Integrity Check",
        "es": "Verificación de Integridad de Datos",
        "fr": "Vérification de l'Intégrité des Données",
        "de": "Datenintegritätsprüfung",
        "it": "Verifica dell'Integrità dei Dati",
        "nl": "Gegevensintegriteitscontrole",
        "lb": "Dateintegritéitspréiwung"
    },
    "Restaurar Cópia de Segurança": {
        "pt": "Restaurar Cópia de Segurança",
        "en": "Restore Backup",
        "es": "Restaurar Copia de Seguridad",
        "fr": "Restaurer la Sauvegarde",
        "de": "Sicherung wiederherstellen",
        "it": "Ripristina Backup",
        "nl": "Back-up herstellen",
        "lb": "Sécherungskopie restauréieren"
    },
    "Importar Cópia de Segurança (JSON)": {
        "pt": "Importar Cópia de Segurança (JSON)",
        "en": "Import Backup (JSON)",
        "es": "Importar Copia de Seguridad (JSON)",
        "fr": "Importer une Sauvegarde (JSON)",
        "de": "Sicherung importieren (JSON)",
        "it": "Importa Backup (JSON)",
        "nl": "Back-up importeren (JSON)",
        "lb": "Sécherungskopie importéieren (JSON)"
    },
    "Base de Dados Segura & Íntegra": {
        "pt": "Base de Dados Segura & Íntegra",
        "en": "Database Secure & Healthy",
        "es": "Base de Datos Segura e Íntegra",
        "fr": "Base de Données Sécurisée & Intègre",
        "de": "Datenbank sicher und intakt",
        "it": "Database Sicuro e Integro",
        "nl": "Database veilig en intact",
        "lb": "Datebank sécher & intakt"
    },
    "Tamanho da Base de Dados:": {
        "pt": "Tamanho da Base de Dados:",
        "en": "Database Size:",
        "es": "Tamaño de la Base de Datos:",
        "fr": "Taille de la Base de Données:",
        "de": "Datenbankgröße:",
        "it": "Dimensione del Database:",
        "nl": "Databasegrootte:",
        "lb": "Datebankgréisst:"
    },
    "Total de Animais:": {
        "pt": "Total de Animais:",
        "en": "Total Pets:",
        "es": "Total de Mascotas:",
        "fr": "Total d'Animaux:",
        "de": "Gesamtzahl der Tiere:",
        "it": "Totale Animali:",
        "nl": "Totaal Aantal Dieren:",
        "lb": "Total Déieren:"
    },
    "Registos Clínicos:": {
        "pt": "Registos Clínicos:",
        "en": "Clinical Records:",
        "es": "Registros Clínicos:",
        "fr": "Dossiers Cliniques:",
        "de": "Klinische Aufzeichnungen:",
        "it": "Cartelle Cliniche:",
        "nl": "Klinische Dossiers:",
        "lb": "Klinesch Dossieren:"
    },
    "Última Cópia de Segurança:": {
        "pt": "Última Cópia de Segurança:",
        "en": "Last Backup:",
        "es": "Última Copia de Seguridad:",
        "fr": "Dernière Sauvegarde:",
        "de": "Letzte Sicherung:",
        "it": "Ultimo Backup:",
        "nl": "Laatste Back-up:",
        "lb": "Lescht Sécherungskopie:"
    },
    "Nunca realizada": {
        "pt": "Nunca realizada",
        "en": "Never performed",
        "es": "Nunca realizada",
        "fr": "Jamais effectuée",
        "de": "Noch nie durchgeführt",
        "it": "Mai eseguita",
        "nl": "Nooit uitgevoerd",
        "lb": "Ni gemaach"
    },
    "Cópia Restaurada com Sucesso": {
        "pt": "Cópia Restaurada com Sucesso",
        "en": "Backup Restored Successfully",
        "es": "Copia Restaurada con Éxito",
        "fr": "Sauvegarde Restaurée avec Succès",
        "de": "Sicherung erfolgreich wiederhergestellt",
        "it": "Backup Ripristinato con Successo",
        "nl": "Back-up succesvol hersteld",
        "lb": "Sécherungskopie erfollegräich restauréiert"
    },
    "%d animais importados com sucesso.": {
        "pt": "%d animais importados com sucesso.",
        "en": "%d pets imported successfully.",
        "es": "%d mascotas importadas con éxito.",
        "fr": "%d animaux importés avec succès.",
        "de": "%d Tiere erfolgreich importiert.",
        "it": "%d animali importati con successo.",
        "nl": "%d dieren succesvol geïmporteerd.",
        "lb": "%d Déieren erfollegräich importéiert."
    },
    "Erro ao ler ficheiro de backup": {
        "pt": "Erro ao ler ficheiro de backup",
        "en": "Error reading backup file",
        "es": "Error al leer el archivo de copia de seguridad",
        "fr": "Erreur lors de la lecture du fichier de sauvegarde",
        "de": "Fehler beim Lesen der Sicherungsdatei",
        "it": "Errore durante la lettura del file di backup",
        "nl": "Fout bij het lezen van back-upbestand",
        "lb": "Feeler beim Liese vun der Sécherungsdatei"
    },
    "Verificar Base de Dados": {
        "pt": "Verificar Base de Dados",
        "en": "Verify Database",
        "es": "Verificar Base de Datos",
        "fr": "Vérifier la Base de Données",
        "de": "Datenbank überprüfen",
        "it": "Verifica Database",
        "nl": "Database verifiëren",
        "lb": "Datebank iwwerpréiwen"
    },
    "Nenhum erro encontrado": {
        "pt": "Nenhum erro encontrado",
        "en": "No errors found",
        "es": "No se encontraron errores",
        "fr": "Aucune erreur trouvée",
        "de": "Keine Fehler gefunden",
        "it": "Nessun errore trovato",
        "nl": "Geen fouten gevonden",
        "lb": "Keng Feeler fonnt"
    },
    "Estado dos Dados & Integridade": {
        "pt": "Estado dos Dados & Integridade",
        "en": "Data Status & Integrity",
        "es": "Estado de los Datos e Integridad",
        "fr": "État des Données & Intégrité",
        "de": "Datenstatus & Integrität",
        "it": "Stato dei Dati & Integrità",
        "nl": "Gegevensstatus & Integriteit",
        "lb": "Datestatus & Integritéit"
    }
}

# 1. Update translations.json
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

print("Updated translations and XMLs for backup integrity check")
