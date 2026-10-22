#!/usr/bin/env python3
import json
import os
import xml.etree.ElementTree as ET

NEW_TRANSLATIONS = {
    "Sincronizar com Computador": {
        "pt": "Sincronizar com Computador",
        "en": "Sync with Computer",
        "es": "Sincronizar con el Ordenador",
        "fr": "Synchroniser avec l'Ordinateur",
        "de": "Mit Computer synchronisieren",
        "it": "Sincronizza con il Computer",
        "nl": "Synchroniseren met computer",
        "lb": "Mat Computer synchroniséieren"
    },
    "Transfira e sincronize os seus pets com a versão Windows pela rede Wi-Fi.": {
        "pt": "Transfira e sincronize os seus pets com a versão Windows pela rede Wi-Fi.",
        "en": "Transfer and sync your pets with the Windows version over Wi-Fi.",
        "es": "Transfiera y sincronice sus mascotas con la versión de Windows por Wi-Fi.",
        "fr": "Transférez et synchronisez vos animaux avec la version Windows via Wi-Fi.",
        "de": "Übertragen und synchronisieren Sie Ihre Haustiere mit der Windows-Version über WLAN.",
        "it": "Trasferisci e sincronizza i tuoi animali con la versione Windows tramite Wi-Fi.",
        "nl": "Zet uw huisdieren over en synchroniseer met de Windows-versie via Wi-Fi.",
        "lb": "Transferéiert a synchroniséiert Är Déieren mat der Windows-Versioun iwwer Wi-Fi."
    },
    "Endereço IP do Computador": {
        "pt": "Endereço IP do Computador",
        "en": "Computer IP Address",
        "es": "Dirección IP del Ordenador",
        "fr": "Adresse IP de l'Ordinateur",
        "de": "IP-Adresse des Computers",
        "it": "Indirizzo IP del Computer",
        "nl": "IP-adres van de computer",
        "lb": "IP-Adress vum Computer"
    },
    "Ex: 192.168.1.15 ou código QR": {
        "pt": "Ex: 192.168.1.15 ou código QR",
        "en": "Ex: 192.168.1.15 or QR code",
        "es": "Ej: 192.168.1.15 o código QR",
        "fr": "Ex : 192.168.1.15 ou code QR",
        "de": "Z.B.: 192.168.1.15 oder QR-Code",
        "it": "Es: 192.168.1.15 o codice QR",
        "nl": "Bijv.: 192.168.1.15 of QR-code",
        "lb": "Bsp.: 192.168.1.15 oder QR-Code"
    },
    "PIN de Autorização (Opcional)": {
        "pt": "PIN de Autorização (Opcional)",
        "en": "Authorization PIN (Optional)",
        "es": "PIN de Autorización (Opcional)",
        "fr": "Code PIN d'Autorisation (Facultatif)",
        "de": "Autorisierungs-PIN (Optional)",
        "it": "PIN di Autorizzazione (Opzionale)",
        "nl": "Autorisatie-PIN (Optioneel)",
        "lb": "Autorisatiouns-PIN (Fakultativ)"
    },
    "Sincronizar Agora": {
        "pt": "Sincronizar Agora",
        "en": "Sync Now",
        "es": "Sincronizar Ahora",
        "fr": "Synchroniser Maintenant",
        "de": "Jetzt synchronisieren",
        "it": "Sincronizza Ora",
        "nl": "Nu synchroniseren",
        "lb": "Elo synchroniséieren"
    },
    "Sincronizando dados com o PC...": {
        "pt": "Sincronizando dados com o PC...",
        "en": "Syncing data with PC...",
        "es": "Sincronizando datos con el PC...",
        "fr": "Synchronisation des données avec le PC...",
        "de": "Daten werden mit PC synchronisiert...",
        "it": "Sincronizzazione dati con il PC...",
        "nl": "Gegevens synchroniseren met pc...",
        "lb": "Donnéeë gi mam PC synchroniséiert..."
    },
    "Sincronização Concluída com Sucesso!": {
        "pt": "Sincronização Concluída com Sucesso!",
        "en": "Sync Completed Successfully!",
        "es": "¡Sincronización Completada con Éxito!",
        "fr": "Synchronisation Réussie !",
        "de": "Synchronisierung erfolgreich abgeschlossen!",
        "it": "Sincronizzazione Completata con Successo!",
        "nl": "Synchronisatie succesvol voltooid!",
        "lb": "Synchronisatioun erfollegräich ofgeschloss!"
    },
    "Falha na sincronização. Verifique a rede Wi-Fi e o IP do PC.": {
        "pt": "Falha na sincronização. Verifique a rede Wi-Fi e o IP do PC.",
        "en": "Sync failed. Check your Wi-Fi network and PC IP.",
        "es": "Error de sincronización. Compruebe la red Wi-Fi y la IP del PC.",
        "fr": "Échec de synchronisation. Vérifiez le réseau Wi-Fi et l'IP du PC.",
        "de": "Synchronisierung fehlgeschlagen. Überprüfen Sie WLAN und PC-IP.",
        "it": "Sincronizzazione non riuscita. Verifica la rete Wi-Fi e l'IP del PC.",
        "nl": "Synchronisatie mislukt. Controleer Wi-Fi en pc-IP.",
        "lb": "Synchronisatioun feelgeschloen. Iwwerpréift Wi-Fi an PC-IP."
    },
    "Como funciona a sincronização?": {
        "pt": "Como funciona a sincronização?",
        "en": "How does sync work?",
        "es": "¿Cómo funciona la sincronización?",
        "fr": "Comment fonctionne la synchronisation ?",
        "de": "Wie funktioniert die Synchronisierung?",
        "it": "Come funziona la sincronizzazione?",
        "nl": "Hoe werkt de synchronisatie?",
        "lb": "Wéi funktionéiert d'Synchronisatioun?"
    },
    "1. Abra o VitalPet no seu computador Windows.": {
        "pt": "1. Abra o VitalPet no seu computador Windows.",
        "en": "1. Open VitalPet on your Windows computer.",
        "es": "1. Abra VitalPet en su ordenador Windows.",
        "fr": "1. Ouvrez VitalPet sur votre ordinateur Windows.",
        "de": "1. Öffnen Sie VitalPet auf Ihrem Windows-Computer.",
        "it": "1. Apri VitalPet sul tuo computer Windows.",
        "nl": "1. Open VitalPet op uw Windows-computer.",
        "lb": "1. Maacht VitalPet op Ärem Windows-Computer op."
    },
    "2. Verifique se o telemóvel e o PC estão no mesmo Wi-Fi.": {
        "pt": "2. Verifique se o telemóvel e o PC estão no mesmo Wi-Fi.",
        "en": "2. Ensure both phone and PC are on the same Wi-Fi.",
        "es": "2. Asegúrese de que el móvil y el PC están en la misma Wi-Fi.",
        "fr": "2. Assurez-vous que le téléphone et le PC sont sur le même Wi-Fi.",
        "de": "2. Stellen Sie sicher, dass Smartphone und PC im selben WLAN sind.",
        "it": "2. Assicurati che telefono e PC siano sulla stessa rete Wi-Fi.",
        "nl": "2. Zorg ervoor dat telefoon en pc op dezelfde Wi-Fi zitten.",
        "lb": "2. Gitt sécher, datt Handy a PC am selwechte Wi-Fi sinn."
    },
    "3. Introduza o IP ou leia o QR Code apresentado no ecrã do PC.": {
        "pt": "3. Introduza o IP ou leia o QR Code apresentado no ecrã do PC.",
        "en": "3. Enter the IP or scan the QR Code shown on the PC screen.",
        "es": "3. Introduzca la IP o escanee el código QR mostrado en la pantalla del PC.",
        "fr": "3. Entrez l'IP ou scannez le QR Code affiché sur l'écran du PC.",
        "de": "3. Geben Sie die IP ein oder scannen Sie den QR-Code auf dem PC-Bildschirm.",
        "it": "3. Inserisci l'IP o scansiona il codice QR sullo schermo del PC.",
        "nl": "3. Voer het IP-adres in of scan de QR-code op het pc-scherm.",
        "lb": "3. Gitt d'IP an oder scannt de QR-Code um PC-Bildschierm."
    },
    "Animais sincronizados:": {
        "pt": "Animais sincronizados:",
        "en": "Synced pets:",
        "es": "Mascotas sincronizadas:",
        "fr": "Animaux synchronisés :",
        "de": "Synchronisierte Haustiere:",
        "it": "Animali sincronizzati:",
        "nl": "Gesynchroniseerde huisdieren:",
        "lb": "Synchroniséiert Déieren:"
    },
    "Sincronização com Dispositivos Móveis": {
        "pt": "Sincronização com Dispositivos Móveis",
        "en": "Mobile Device Synchronization",
        "es": "Sincronización con Dispositivos Móviles",
        "fr": "Synchronisation avec Appareils Mobiles",
        "de": "Synchronisierung mit Mobilgeräten",
        "it": "Sincronizzazione con Dispositivi Mobili",
        "nl": "Synchronisatie met mobiele apparaten",
        "lb": "Synchronisatioun mat mobillen Apparater"
    },
    "Conecte o seu computador Windows aos seus dispositivos Android e iPhone.": {
        "pt": "Conecte o seu computador Windows aos seus dispositivos Android e iPhone.",
        "en": "Connect your Windows computer to your Android and iPhone devices.",
        "es": "Conecte su ordenador Windows a sus dispositivos Android e iPhone.",
        "fr": "Connectez votre ordinateur Windows à vos appareils Android et iPhone.",
        "de": "Verbinden Sie Ihren Windows-Computer mit Ihren Android- und iPhone-Geräten.",
        "it": "Collega il tuo computer Windows ai tuoi dispositivi Android e iPhone.",
        "nl": "Verbind uw Windows-computer met uw Android- en iPhone-apparaten.",
        "lb": "Verbënnt Äre Windows-Computer mat Ären Android- an iPhone-Apparater."
    },
    "Servidor Ativo (Wi-Fi)": {
        "pt": "Servidor Ativo (Wi-Fi)",
        "en": "Server Active (Wi-Fi)",
        "es": "Servidor Activo (Wi-Fi)",
        "fr": "Serveur Actif (Wi-Fi)",
        "de": "Server Aktiv (WLAN)",
        "it": "Server Attivo (Wi-Fi)",
        "nl": "Server Actief (Wi-Fi)",
        "lb": "Server Aktiv (Wi-Fi)"
    },
    "Servidor Parado": {
        "pt": "Servidor Parado",
        "en": "Server Stopped",
        "es": "Servidor Detenido",
        "fr": "Serveur Arrêté",
        "de": "Server Gestoppt",
        "it": "Server Fermo",
        "nl": "Server Gestopt",
        "lb": "Server Gestoppt"
    },
    "Emparelhamento por QR Code": {
        "pt": "Emparelhamento por QR Code",
        "en": "QR Code Pairing",
        "es": "Emparejamiento por Código QR",
        "fr": "Jumelage par QR Code",
        "de": "QR-Code-Kopplung",
        "it": "Accoppiamento tramite Codice QR",
        "nl": "Koppeling via QR-code",
        "lb": "Kopplung iwwer QR-Code"
    },
    "Aponte a câmara do aplicativo móvel para sincronizar": {
        "pt": "Aponte a câmara do aplicativo móvel para sincronizar",
        "en": "Point mobile app camera to sync",
        "es": "Apunte la cámara de la aplicación móvil para sincronizar",
        "fr": "Pointez la caméra de l'application mobile pour synchroniser",
        "de": "Richten Sie die Handykamera aus, um zu synchronisieren",
        "it": "Punta la fotocamera dell'app mobile per sincronizzare",
        "nl": "Richt de camera van de mobiele app om te synchroniseren",
        "lb": "Richt d'Handyskamera aus fir ze synchroniséieren"
    },
    "Endereço IP Local:": {
        "pt": "Endereço IP Local:",
        "en": "Local IP Address:",
        "es": "Dirección IP Local:",
        "fr": "Adresse IP Locale :",
        "de": "Lokale IP-Adresse:",
        "it": "Indirizzo IP Locale:",
        "nl": "Lokaal IP-adres:",
        "lb": "Lokal IP-Adress:"
    },
    "Porta do Serviço:": {
        "pt": "Porta do Serviço:",
        "en": "Service Port:",
        "es": "Puerto del Servicio:",
        "fr": "Port du Service :",
        "de": "Dienst-Port:",
        "it": "Porta del Servizio:",
        "nl": "Servicepoort:",
        "lb": "Déngscht-Port:"
    },
    "PIN de Autorização:": {
        "pt": "PIN de Autorização:",
        "en": "Authorization PIN:",
        "es": "PIN de Autorización:",
        "fr": "Code PIN d'Autorisation :",
        "de": "Autorisierungs-PIN:",
        "it": "PIN di Autorizzazione:",
        "nl": "Autorisatie-PIN:",
        "lb": "Autorisatiouns-PIN:"
    },
    "Regenerar Código e Reiniciar": {
        "pt": "Regenerar Código e Reiniciar",
        "en": "Regenerate Code and Restart",
        "es": "Regenerar Código y Reiniciar",
        "fr": "Régénérer le Code et Redémarrer",
        "de": "Code neu generieren und neu starten",
        "it": "Rigenera Codice e Riavvia",
        "nl": "Code opnieuw genereren en herstarten",
        "lb": "Code nei generéieren an nei starten"
    },
    "Como Sincronizar em 3 Passos:": {
        "pt": "Como Sincronizar em 3 Passos:",
        "en": "How to Sync in 3 Steps:",
        "es": "Cómo Sincronizar en 3 Pasos:",
        "fr": "Comment Synchroniser en 3 Étapes :",
        "de": "So synchronisieren Sie in 3 Schritten:",
        "it": "Come Sincronizzare in 3 Passaggi:",
        "nl": "Hoe te synchroniseren in 3 stappen:",
        "lb": "Wéi synchroniséieren an 3 Schrëtt:"
    },
    "Mesma Rede Wi-Fi": {
        "pt": "Mesma Rede Wi-Fi",
        "en": "Same Wi-Fi Network",
        "es": "Misma Red Wi-Fi",
        "fr": "Même Réseau Wi-Fi",
        "de": "Dasselbe WLAN-Netzwerk",
        "it": "Stessa Rete Wi-Fi",
        "nl": "Hetzelfde Wi-Fi-netwerk",
        "lb": "Datselwecht Wi-Fi-Netzwierk"
    },
    "Certifique-se de que o computador e o telemóvel estão conectados ao mesmo router/Wi-Fi.": {
        "pt": "Certifique-se de que o computador e o telemóvel estão conectados ao mesmo router/Wi-Fi.",
        "en": "Make sure both computer and phone are connected to the same Wi-Fi router.",
        "es": "Asegúrese de que el ordenador y el móvil están conectados al mismo router/Wi-Fi.",
        "fr": "Assurez-vous que l'ordinateur et le téléphone sont connectés au même routeur/Wi-Fi.",
        "de": "Stellen Sie sicher, dass Computer und Smartphone mit demselben Router/WLAN verbunden sind.",
        "it": "Assicurati che computer e telefono siano collegati allo stesso router/Wi-Fi.",
        "nl": "Zorg ervoor dat computer en telefoon zijn verbonden met dezelfde router/Wi-Fi.",
        "lb": "Gitt sécher, datt Computer an Handy um selwechte Router/Wi-Fi verbonne sinn."
    },
    "Abra a App Móvel": {
        "pt": "Abra a App Móvel",
        "en": "Open Mobile App",
        "es": "Abra la App Móvil",
        "fr": "Ouvrez l'App Mobile",
        "de": "Mobile App öffnen",
        "it": "Apri l'App Mobile",
        "nl": "Open mobiele app",
        "lb": "Mobil App opmaachen"
    },
    "No Android ou iPhone, aceda ao menu Definições > Sincronizar com Computador.": {
        "pt": "No Android ou iPhone, aceda ao menu Definições > Sincronizar com Computador.",
        "en": "On Android or iPhone, go to Settings > Sync with Computer.",
        "es": "En Android o iPhone, vaya a Ajustes > Sincronizar con el Ordenador.",
        "fr": "Sur Android ou iPhone, allez dans Paramètres > Synchroniser avec l'Ordinateur.",
        "de": "Gehen Sie auf Android oder iPhone zu Einstellungen > Mit Computer synchronisieren.",
        "it": "Su Android o iPhone, vai su Impostazioni > Sincronizza con il Computer.",
        "nl": "Ga op Android of iPhone naar Instellingen > Synchroniseren met computer.",
        "lb": "Gitt op Android oder iPhone op Astellungen > Mat Computer synchroniséieren."
    },
    "Aponte a Câmara": {
        "pt": "Aponte a Câmara",
        "en": "Point Camera",
        "es": "Apunte la Cámara",
        "fr": "Pointez la Caméra",
        "de": "Kamera ausrichten",
        "it": "Inquadra con la Fotocamera",
        "nl": "Richt de camera",
        "lb": "Kamera ausriichten"
    },
    "Prima 'Escanear QR Code'. Todos os animais, vacinas e pesos serão sincronizados instantaneamente.": {
        "pt": "Prima 'Escanear QR Code'. Todos os animais, vacinas e pesos serão sincronizados instantaneamente.",
        "en": "Tap 'Scan QR Code'. All pets, vaccines and weights will be synced instantly.",
        "es": "Pulse 'Escanear Código QR'. Todas las mascotas, vacunas y pesos se sincronizarán al instante.",
        "fr": "Appuyez sur 'Scanner le QR Code'. Tous les animaux, vaccins et poids seront synchronisés instantanément.",
        "de": "Tippen Sie auf 'QR-Code scannen'. Alle Tiere, Impfungen und Gewichte werden sofort synchronisiert.",
        "it": "Tocca 'Scansiona Codice QR'. Tutti gli animali, vaccini e pesi saranno sincronizzati all'istante.",
        "nl": "Tik op 'QR-code scannen'. Alle dieren, vaccins en gewichten worden direct gesynchroniseerd.",
        "lb": "Tippt op 'QR-Code scannen'. All Déieren, Impfungen a Gewiichter gi direkt synchroniséiert."
    },
    "Plataformas Suportadas": {
        "pt": "Plataformas Suportadas",
        "en": "Supported Platforms",
        "es": "Plataformas Compatibles",
        "fr": "Plateformes Prises en Charge",
        "de": "Unterstützte Plattformen",
        "it": "Piattaforme Supportate",
        "nl": "Ondersteunde platforms",
        "lb": "Ënnerstëtzt Plattformen"
    },
    "100% Compatível": {
        "pt": "100% Compatível",
        "en": "100% Compatible",
        "es": "100% Compatible",
        "fr": "100% Compatible",
        "de": "100% Kompatibel",
        "it": "100% Compatibile",
        "nl": "100% Compatibel",
        "lb": "100% Kompatibel"
    },
    "Estado de Sincronização": {
        "pt": "Estado de Sincronização",
        "en": "Sync Status",
        "es": "Estado de Sincronización",
        "fr": "État de Synchronisation",
        "de": "Synchronisierungsstatus",
        "it": "Stato di Sincronizzazione",
        "nl": "Synchronisatiestatus",
        "lb": "Synchronisatiounsstatus"
    },
    "Nenhuma sincronização realizada nesta sessão.": {
        "pt": "Nenhuma sincronização realizada nesta sessão.",
        "en": "No synchronization performed in this session.",
        "es": "Ninguna sincronización realizada en esta sesión.",
        "fr": "Aucune synchronisation effectuée au cours de cette session.",
        "de": "In dieser Sitzung wurde keine Synchronisierung durchgeführt.",
        "it": "Nessuna sincronizzazione eseguita in questa sessione.",
        "nl": "Geen synchronisatie uitgevoerd in deze sessie.",
        "lb": "Keng Synchronisatioun an dëser Sessioun duerchgefouert."
    },
    "Ficheiro de Backup Universal (.vitalpet)": {
        "pt": "Ficheiro de Backup Universal (.vitalpet)",
        "en": "Universal Backup File (.vitalpet)",
        "es": "Archivo de Copia de Seguridad Universal (.vitalpet)",
        "fr": "Fichier de Sauvegarde Universel (.vitalpet)",
        "de": "Universelle Sicherungsdatei (.vitalpet)",
        "it": "File di Backup Universale (.vitalpet)",
        "nl": "Universeel back-upbestand (.vitalpet)",
        "lb": "Universell Backup-Datei (.vitalpet)"
    },
    "Pode também transferir dados manualmente via ficheiro entre o PC e o telemóvel.": {
        "pt": "Pode também transferir dados manualmente via ficheiro entre o PC e o telemóvel.",
        "en": "You can also transfer data manually via file between PC and phone.",
        "es": "También puede transferir datos manualmente mediante archivo entre el PC y el móvil.",
        "fr": "Vous pouvez également transférer manuellement des données via fichier entre le PC et le téléphone.",
        "de": "Sie können Daten auch manuell per Datei zwischen PC und Smartphone übertragen.",
        "it": "Puoi anche trasferire i dati manualmente tramite file tra PC e telefono.",
        "nl": "U kunt gegevens ook handmatig via een bestand overdragen tussen pc en telefoon.",
        "lb": "Dir kënnt Donnéeën och manuell iwwer Datei tëscht PC an Handy transferéieren."
    },
    "Exportar Backup": {
        "pt": "Exportar Backup",
        "en": "Export Backup",
        "es": "Exportar Copia",
        "fr": "Exporter Sauvegarde",
        "de": "Backup exportieren",
        "it": "Esporta Backup",
        "nl": "Back-up exporteren",
        "lb": "Backup exportéieren"
    },
    "Importar Backup": {
        "pt": "Importar Backup",
        "en": "Import Backup",
        "es": "Importar Copia",
        "fr": "Importer Sauvegarde",
        "de": "Backup importieren",
        "it": "Importa Backup",
        "nl": "Back-up importeren",
        "lb": "Backup importéieren"
    }
}

base_dir = "/Users/renatoreis/Desktop/VitalPetAndroid"
json_path = os.path.join(base_dir, "app/src/main/assets/translations.json")

with open(json_path, "r", encoding="utf-8") as f:
    data = json.load(f)

for k, v in NEW_TRANSLATIONS.items():
    data[k] = v

with open(json_path, "w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print(f"Updated {json_path} with {len(NEW_TRANSLATIONS)} new keys. Total keys: {len(data)}")
