import json
import re

compliance_translations = {
    "Sobre a Aplicação & Privacidade": {
        "pt": "Sobre a Aplicação & Privacidade",
        "en": "About App & Privacy",
        "es": "Acerca de la App y Privacidad",
        "fr": "À Propos de l'App & Confidentialité",
        "de": "Über die App & Datenschutz",
        "it": "Info sull'App & Privacy",
        "nl": "Over de App & Privacy",
        "lb": "Iwwer d'App & Privatsphär"
    },
    "Versão 1.0.0 (Build 1) • Google Play Ready": {
        "pt": "Versão 1.0.0 (Build 1) • Google Play Ready",
        "en": "Version 1.0.0 (Build 1) • Google Play Ready",
        "es": "Versión 1.0.0 (Build 1) • Google Play Ready",
        "fr": "Version 1.0.0 (Build 1) • Google Play Ready",
        "de": "Version 1.0.0 (Build 1) • Google Play Ready",
        "it": "Versione 1.0.0 (Build 1) • Google Play Ready",
        "nl": "Versie 1.0.0 (Build 1) • Google Play Ready",
        "lb": "Versioun 1.0.0 (Build 1) • Google Play Ready"
    },
    "Diretrizes oficiais WSAVA e NRC para nutrição e bem-estar animal. Compatível com as normas de segurança e privacidade da Google.": {
        "pt": "Diretrizes oficiais WSAVA e NRC para nutrição e bem-estar animal. Compatível com as normas de segurança e privacidade da Google.",
        "en": "Official WSAVA and NRC guidelines for pet nutrition and wellness. Fully compliant with Google security and privacy standards.",
        "es": "Pautas oficiales de WSAVA y NRC para nutrición y bienestar animal. Compatible con las normas de seguridad y privacidad de Google.",
        "fr": "Directives officielles WSAVA et NRC pour la nutrition et le bien-être animal. Conforme aux normes de sécurité et confidentialité de Google.",
        "de": "Offizielle WSAVA- und NRC-Richtlinien für Tierernährung und Wohlbefinden. Konform mit den Sicherheits- und Datenschutzstandards von Google.",
        "it": "Linee guida ufficiali WSAVA e NRC per nutrizione e benessere animale. Compatibile con gli standard di sicurezza e privacy di Google.",
        "nl": "Officiële WSAVA- en NRC-richtlijnen voor voeding en welzijn van huisdieren. Voldoet aan de veiligheids- en privacynormen van Google.",
        "lb": "Offiziell WSAVA- an NRC-Richtlinn fir Déierenernährung a Wuelbefannen. Kompatibel mat de Sécherheets- a Privatsphärnorme vu Google."
    },
    "Segurança de Dados no Dispositivo": {
        "pt": "Segurança de Dados no Dispositivo",
        "en": "On-Device Data Safety",
        "es": "Seguridad de Datos en el Dispositivo",
        "fr": "Sécurité des Données sur l'Appareil",
        "de": "Datensicherheit auf dem Gerät",
        "it": "Sicurezza dei Dati sul Dispositivo",
        "nl": "Gegevensbeveiliging op het Apparaat",
        "lb": "Datesécherheet um Apparat"
    },
    "Segurança & Proteção de Dados": {
        "pt": "Segurança & Proteção de Dados",
        "en": "Security & Data Protection",
        "es": "Seguridad y Protección de Datos",
        "fr": "Sécurité & Protection des Données",
        "de": "Sicherheit & Datenschutz",
        "it": "Sicurezza & Protezione dei Dati",
        "nl": "Beveiliging & Gegevensbescherming",
        "lb": "Sécherheet & Dateschutz"
    },
    "O VitalPet armazena todos os registos clínicos, dados demográficos, notas e fotografias diretamente no armazenamento protegido do seu dispositivo Android.": {
        "pt": "O VitalPet armazena todos os registos clínicos, dados demográficos, notas e fotografias diretamente no armazenamento protegido do seu dispositivo Android.",
        "en": "VitalPet stores all clinical records, demographics, notes, and photos directly in protected storage on your Android device.",
        "es": "VitalPet almacena todos los registros clínicos, datos demográficos, notas y fotos directamente en el almacenamiento protegido de su dispositivo Android.",
        "fr": "VitalPet stocke tous les dossiers cliniques, données démographiques, notes et photos directement dans le stockage protégé de votre appareil Android.",
        "de": "VitalPet speichert alle klinischen Aufzeichnungen, Stammdaten, Notizen und Fotos direkt im geschützten Speicher Ihres Android-Geräts.",
        "it": "VitalPet memorizza tutte le cartelle cliniche, dati demografici, note e foto direttamente nella memoria protetta del tuo dispositivo Android.",
        "nl": "VitalPet bewaart alle klinische dossiers, demografische gegevens, notities en foto's direct in de beveiligde opslag van uw Android-apparaat.",
        "lb": "VitalPet späichert all klinesch Dossieren, Stammdaten, Notizen a Fotoen direkt am geschützte Späicher vun Ärem Android-Apparat."
    },
    "• Não vendemos nem partilhamos os seus dados pessoais com terceiros.": {
        "pt": "• Não vendemos nem partilhamos os seus dados pessoais com terceiros.",
        "en": "• We do not sell or share your personal data with third parties.",
        "es": "• No vendemos ni compartimos sus datos personales con terceros.",
        "fr": "• Nous ne vendons ni ne partageons vos données personnelles avec des tiers.",
        "de": "• Wir verkaufen oder teilen Ihre personenbezogenen Daten nicht mit Dritten.",
        "it": "• Non vendiamo né condividiamo i tuoi dati personali con terze parti.",
        "nl": "• Wij verkopen of delen uw persoonlijke gegevens niet met derden.",
        "lb": "• Mir verkafen oder deelen Är perséinlech Donnéeën net mat Drëtten."
    },
    "• Acesso à localização é usado exclusivamente em primeiro plano para encontrar hospitais veterinários 24h próximos.": {
        "pt": "• Acesso à localização é usado exclusivamente em primeiro plano para encontrar hospitais veterinários 24h próximos.",
        "en": "• Location access is used exclusively in foreground to locate nearby 24h veterinary emergency hospitals.",
        "es": "• El acceso a la ubicación se usa exclusivamente en primer plano para encontrar hospitales veterinarios 24h cercanos.",
        "fr": "• L'accès à la localisation est utilisé exclusivement au premier plan pour trouver des hôpitaux vétérinaires 24h proches.",
        "de": "• Der Standortzugriff wird ausschließlich im Vordergrund verwendet, um nahegelegene 24h-Tierkliniken zu finden.",
        "it": "• L'accesso alla posizione è utilizzato esclusivamente in primo piano per individuare ospedali veterinari 24h vicini.",
        "nl": "• Locatietoegang wordt uitsluitend op de voorgrond gebruikt om 24-uurs dierenklinieken in de buurt te vinden.",
        "lb": "• Standuertzougang gëtt exklusiv am Virgronn benotzt fir no 24h Déiereklinicken ze fannen."
    },
    "• As cópias de segurança em ficheiro JSON permanecem sob o seu controlo absoluto.": {
        "pt": "• As cópias de segurança em ficheiro JSON permanecem sob o seu controlo absoluto.",
        "en": "• JSON backup files remain under your absolute control.",
        "es": "• Las copias de seguridad en archivo JSON permanecen bajo su absoluto control.",
        "fr": "• Les sauvegardes de fichiers JSON restent sous votre contrôle absolu.",
        "de": "• JSON-Sicherungsdateien bleiben unter Ihrer absoluten Kontrolle.",
        "it": "• I file di backup JSON rimangono sotto il tuo assoluto controllo.",
        "nl": "• JSON-reservekopiebestanden blijven onder uw absolute controle.",
        "lb": "• JSON-Backupdateien bleiwen ënner Ärer absolutter Kontroll."
    },
    "Compreendi": {
        "pt": "Compreendi",
        "en": "Understood",
        "es": "Entendido",
        "fr": "Compris",
        "de": "Verstanden",
        "it": "Ho Capito",
        "nl": "Begrepen",
        "lb": "Verstan"
    }
}

# 1. Update translations.json
with open("app/src/main/assets/translations.json", "r", encoding="utf-8") as f:
    translations = json.load(f)

for k, v in compliance_translations.items():
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
    for k, trans in compliance_translations.items():
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

print(f"Added {len(compliance_translations)} compliance translation keys across all 8 languages successfully.")
