#!/usr/bin/env python3
"""
Adds subscription and paywall translations in all 8 required languages
and syncs both translations.json and strings.xml.
"""
import json
import os
import re

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TRANSLATIONS_JSON = os.path.join(BASE_DIR, "app/src/main/assets/translations.json")
RES_DIR = os.path.join(BASE_DIR, "app/src/main/res")

LANG_FILES = {
    "pt": os.path.join(RES_DIR, "values/strings.xml"),
    "en": os.path.join(RES_DIR, "values-en/strings.xml"),
    "es": os.path.join(RES_DIR, "values-es/strings.xml"),
    "fr": os.path.join(RES_DIR, "values-fr/strings.xml"),
    "de": os.path.join(RES_DIR, "values-de/strings.xml"),
    "it": os.path.join(RES_DIR, "values-it/strings.xml"),
    "nl": os.path.join(RES_DIR, "values-nl/strings.xml"),
    "lb": os.path.join(RES_DIR, "values-lb/strings.xml")
}

new_translations = {
    "VitalPet Premium": {
        "pt": "VitalPet Premium",
        "en": "VitalPet Premium",
        "es": "VitalPet Premium",
        "fr": "VitalPet Premium",
        "de": "VitalPet Premium",
        "it": "VitalPet Premium",
        "nl": "VitalPet Premium",
        "lb": "VitalPet Premium"
    },
    "Acesso Ilimitado": {
        "pt": "Acesso Ilimitado",
        "en": "Unlimited Access",
        "es": "Acceso Ilimitado",
        "fr": "Accès Illimité",
        "de": "Unbegrenzter Zugang",
        "it": "Accesso Illimitato",
        "nl": "Onbeperkte Toegang",
        "lb": "Onbegrenzten Zougang"
    },
    "Desbloqueie todo o potencial da saúde e nutrição dos seus animais.": {
        "pt": "Desbloqueie todo o potencial da saúde e nutrição dos seus animais.",
        "en": "Unlock the full potential of your pets' health and nutrition.",
        "es": "Desbloquee todo el potencial de la salud y nutrición de sus mascotas.",
        "fr": "Libérez tout le potentiel de la santé et de la nutrition de vos animaux.",
        "de": "Nutzen Sie das volle Potenzial der Gesundheit und Ernährung Ihrer Haustiere.",
        "it": "Sblocca tutto il potenziale della salute e della nutrizione dei tuoi animali.",
        "nl": "Ontgrendel het volledige potentieel van de gezondheid en voeding van uw huisdieren.",
        "lb": "Entdeckt dat vollt Potenzial vun der Gesondheet an Ernärung vun Ären Déieren."
    },
    "Animais Ilimitados": {
        "pt": "Animais Ilimitados",
        "en": "Unlimited Pets",
        "es": "Mascotas Ilimitadas",
        "fr": "Animaux Illimités",
        "de": "Unbegrenzte Haustiere",
        "it": "Animali Illimitati",
        "nl": "Onbeperkt Huisdieren",
        "lb": "Onbegrenzt Déieren"
    },
    "Registe e monitorize quantos animais e pacientes desejar sem restrições.": {
        "pt": "Registe e monitorize quantos animais e pacientes desejar sem restrições.",
        "en": "Register and track as many pets and patients as you want without limits.",
        "es": "Registre y monitorice tantas mascotas y pacientes como desee sin restricciones.",
        "fr": "Enregistrez et suivez autant d'animaux et de patients que vous le souhaitez sans restriction.",
        "de": "Registrieren und überwachen Sie beliebig viele Haustiere und Patienten ohne Einschränkungen.",
        "it": "Registra e monitora tutti gli animali e i pazienti che desideri senza restrizioni.",
        "nl": "Registreer en volg zoveel huisdieren en patiënten als u wilt zonder beperkingen.",
        "lb": "Registréiert a verfollegt esou vill Déieren a Patienten wéi Dir wëllt ouni Aschränkungen."
    },
    "Calculadoras Clínicas Avançadas": {
        "pt": "Calculadoras Clínicas Avançadas",
        "en": "Advanced Clinical Calculators",
        "es": "Calculadoras Clínicas Avanzadas",
        "fr": "Calculateurs Cliniques Avancés",
        "de": "Erweiterte klinische Rechner",
        "it": "Calcolatori Clinici Avanzati",
        "nl": "Geavanceerde Klinische Calculators",
        "lb": "Fortgeschratt klinesch Rechner"
    },
    "Acesso a dosagens farmacológicas em mg/kg e fluidoterapia de emergência.": {
        "pt": "Acesso a dosagens farmacológicas em mg/kg e fluidoterapia de emergência.",
        "en": "Access to pharmacological dosages in mg/kg and emergency fluid therapy.",
        "es": "Acceso a dosificaciones farmacológicas en mg/kg y fluidoterapia de emergencia.",
        "fr": "Accès aux dosages pharmacologiques en mg/kg et à la fluidothérapie d'urgence.",
        "de": "Zugang zu pharmakologischen Dosierungen in mg/kg und Notfall-Flüssigkeitstherapie.",
        "it": "Accesso a dosaggi farmacologici in mg/kg e fluidoterapia d'emergenza.",
        "nl": "Toegang tot farmacologische doseringen in mg/kg en noodvloeistoftherapie.",
        "lb": "Zougang zu pharmakologeschen Doséierungen a mg/kg an Noutfall-Flëssegkeetstherapie."
    },
    "Nutrição Especializada & BARF": {
        "pt": "Nutrição Especializada & BARF",
        "en": "Specialized Nutrition & BARF",
        "es": "Nutrición Especializada y BARF",
        "fr": "Nutrition Spécialisée et BARF",
        "de": "Spezialisierte Ernährung & BARF",
        "it": "Nutrizione Specializzata e BARF",
        "nl": "Gespecialiseerde Voeding & BARF",
        "lb": "Spezialiséiert Ernärung & BARF"
    },
    "Cálculo preciso de dietas naturais BARF e análise de matéria seca e NFE.": {
        "pt": "Cálculo preciso de dietas naturais BARF e análise de matéria seca e NFE.",
        "en": "Precise BARF natural diet calculation and dry matter & NFE analysis.",
        "es": "Cálculo preciso de dietas naturales BARF y análisis de materia seca y ELN.",
        "fr": "Calcul précis des régimes naturels BARF et analyse de matière sèche et ENA.",
        "de": "Präzise Berechnung von BARF-Rohfütterung und Trockenmasse- sowie NfE-Analyse.",
        "it": "Calcolo accurato di diete naturali BARF e analisi di sostanza secca e NFE.",
        "nl": "Nauwkeurige berekening van BARF-natuurvoeding en analyse van droge stof en NFE.",
        "lb": "Präzis Berechnung vu BARF-Naturfütterung an Analyse vun Dréchematière an NFE."
    },
    "Temporizador Clínico Pré-Operatório": {
        "pt": "Temporizador Clínico Pré-Operatório",
        "en": "Pre-Operative Fasting Timer",
        "es": "Temporizador Clínico Preoperatorio",
        "fr": "Minuteur Clinique Préopératoire",
        "de": "Präoperativer Fasten-Timer",
        "it": "Timer Clinico Pre-Operatorio",
        "nl": "Pre-operatieve Vastentimer",
        "lb": "Pre-operative Faaschten-Timer"
    },
    "Monitorização rigorosa das fases de jejum cirúrgico.": {
        "pt": "Monitorização rigorosa das fases de jejum cirúrgico.",
        "en": "Strict monitoring of surgical fasting phases.",
        "es": "Monitorización rigurosa de las fases de ayuno quirúrgico.",
        "fr": "Surveillance rigoureuse des phases de jeûne chirurgical.",
        "de": "Strenge Überwachung der chirurgischen Fastenphasen.",
        "it": "Monitoraggio rigoroso delle fasi di digiuno chirurgico.",
        "nl": "Strikte monitoring van de chirurgische vastenfases.",
        "lb": "Streng Iwwerwaachung vun de chirurgesche Faaschtenphasen."
    },
    "Relatórios em PDF": {
        "pt": "Relatórios em PDF",
        "en": "PDF Reports",
        "es": "Informes en PDF",
        "fr": "Rapports en PDF",
        "de": "PDF-Berichte",
        "it": "Report in PDF",
        "nl": "PDF-rapporten",
        "lb": "PDF-Berichter"
    },
    "Exporte relatórios clínicos e nutricionais para o seu veterinário.": {
        "pt": "Exporte relatórios clínicos e nutricionais para o seu veterinário.",
        "en": "Export clinical and nutritional reports for your veterinarian.",
        "es": "Exporte informes clínicos y nutricionales para su veterinario.",
        "fr": "Exportez des rapports cliniques et nutritionnels pour votre vétérinaire.",
        "de": "Exportieren Sie klinische und ernährungsbezogene Berichte für Ihren Tierarzt.",
        "it": "Esporta report clinici e nutrizionali per il tuo veterinario.",
        "nl": "Exporteer klinische en voedingsrapporten voor uw dierenarts.",
        "lb": "Exportéiert klinesch an ernärungsbezunne Berichter fir Ären Déierendokter."
    },
    "Cópias de Segurança & Nuvem": {
        "pt": "Cópias de Segurança & Nuvem",
        "en": "Backup & Cloud",
        "es": "Copias de Seguridad y Nube",
        "fr": "Sauvegarde et Cloud",
        "de": "Datensicherung & Cloud",
        "it": "Backup e Cloud",
        "nl": "Back-up & Cloud",
        "lb": "Sécherungskopie & Cloud"
    },
    "Proteção contínua e restauro fácil de todos os dados clínicos.": {
        "pt": "Proteção contínua e restauro fácil de todos os dados clínicos.",
        "en": "Continuous protection and easy restoration of all clinical data.",
        "es": "Protección continua y restauración sencilla de todos los datos clínicos.",
        "fr": "Protection continue et restauration facile de toutes les données cliniques.",
        "de": "Kontinuierlicher Schutz und einfache Wiederherstellung aller klinischen Daten.",
        "it": "Protezione continua e ripristino facile di tutti i dati clinici.",
        "nl": "Continue bescherming en eenvoudig herstel van alle klinische gegevens.",
        "lb": "Kontinuéierleche Schutz an einfach Restauratioun vun alle klineschen Donnéeën."
    },
    "Plano Mensal": {
        "pt": "Plano Mensal",
        "en": "Monthly Plan",
        "es": "Plan Mensual",
        "fr": "Forfait Mensuel",
        "de": "Monatsabo",
        "it": "Piano Mensile",
        "nl": "Maandelijks Abonnement",
        "lb": "Monatlecht Abonnement"
    },
    "3,99 € / mês": {
        "pt": "3,99 € / mês",
        "en": "€3.99 / month",
        "es": "3,99 € / mes",
        "fr": "3,99 € / mois",
        "de": "3,99 € / Monat",
        "it": "3,99 € / mese",
        "nl": "€ 3,99 / maand",
        "lb": "3,99 € / Mount"
    },
    "Flexibilidade total, cancele quando desejar.": {
        "pt": "Flexibilidade total, cancele quando desejar.",
        "en": "Full flexibility, cancel anytime.",
        "es": "Flexibilidad total, cancele cuando desee.",
        "fr": "Flexibilité totale, résiliez à tout moment.",
        "de": "Volle Flexibilität, jederzeit kündbar.",
        "it": "Flessibilità totale, cancella quando vuoi.",
        "nl": "Volledige flexibiliteit, op elk moment opzegbaar.",
        "lb": "Ganz Flexibilitéit, jiddwerzäit kënnebar."
    },
    "Plano Anual": {
        "pt": "Plano Anual",
        "en": "Annual Plan",
        "es": "Plan Anual",
        "fr": "Forfait Annuel",
        "de": "Jahresabo",
        "it": "Piano Annuale",
        "nl": "Jaarabonnement",
        "lb": "Joresabonnement"
    },
    "19,99 € / ano": {
        "pt": "19,99 € / ano",
        "en": "€19.99 / year",
        "es": "19,99 € / año",
        "fr": "19,99 € / an",
        "de": "19,99 € / Jahr",
        "it": "19,99 € / anno",
        "nl": "€ 19,99 / jaar",
        "lb": "19,99 € / Joer"
    },
    "Equivale a 1,66 € / mês": {
        "pt": "Equivale a 1,66 € / mês",
        "en": "Only €1.66 / month",
        "es": "Equivale a 1,66 € / mes",
        "fr": "Soit 1,66 € / mois",
        "de": "Nur 1,66 € / Monat",
        "it": "Equivale a 1,66 € / mese",
        "nl": "Slechts € 1,66 / maand",
        "lb": "Nëmmen 1,66 € / Mount"
    },
    "Mais Popular": {
        "pt": "Mais Popular",
        "en": "Most Popular",
        "es": "Más Popular",
        "fr": "Plus Populaire",
        "de": "Am beliebtesten",
        "it": "Più Popolare",
        "nl": "Meest Populair",
        "lb": "Am meeschte beléift"
    },
    "Poupe 58%": {
        "pt": "Poupe 58%",
        "en": "Save 58%",
        "es": "Ahorre 58%",
        "fr": "Économisez 58%",
        "de": "58% sparen",
        "it": "Risparmia il 58%",
        "nl": "Bespaar 58%",
        "lb": "Spuert 58%"
    },
    "7 Dias Grátis": {
        "pt": "7 Dias Grátis",
        "en": "7 Days Free",
        "es": "7 Días Gratis",
        "fr": "7 Jours Gratuits",
        "de": "7 Tage kostenlos",
        "it": "7 Giorni Gratis",
        "nl": "7 Dagen Gratis",
        "lb": "7 Deeg gratis"
    },
    "Acesso Vitalício": {
        "pt": "Acesso Vitalício",
        "en": "Lifetime Access",
        "es": "Acceso Vitalicio",
        "fr": "Accès à Vie",
        "de": "Lebenslanger Zugang",
        "it": "Accesso a Vita",
        "nl": "Levenslange Toegang",
        "lb": "Liewenslaange Zougang"
    },
    "49,99 €": {
        "pt": "49,99 €",
        "en": "€49.99",
        "es": "49,99 €",
        "fr": "49,99 €",
        "de": "49,99 €",
        "it": "49,99 €",
        "nl": "€ 49,99",
        "lb": "49,99 €"
    },
    "Pagamento único, seu para sempre sem mensalidades.": {
        "pt": "Pagamento único, seu para sempre sem mensalidades.",
        "en": "One-time payment, yours forever with no monthly fees.",
        "es": "Pago único, suyo para siempre sin mensualidades.",
        "fr": "Paiement unique, à vous pour toujours sans abonnement.",
        "de": "Einmalzahlung, für immer ohne wiederkehrende Gebühren.",
        "it": "Pagamento unico, tuo per sempre senza costi mensili.",
        "nl": "Eenmalige betaling, voor altijd de jouwe zonder abonnementskosten.",
        "lb": "Eemoleg Bezuelung, fir ëmmer äert ouni monatlech Käschten."
    },
    "Melhor Investimento": {
        "pt": "Melhor Investimento",
        "en": "Best Value",
        "es": "Mejor Inversión",
        "fr": "Meilleur Choix",
        "de": "Bester Wert",
        "it": "Miglior Scelta",
        "nl": "Beste Keuze",
        "lb": "Bescht Wiel"
    },
    "Começar Teste Grátis de 7 Dias": {
        "pt": "Começar Teste Grátis de 7 Dias",
        "en": "Start 7-Day Free Trial",
        "es": "Comenzar Prueba Gratis de 7 Días",
        "fr": "Commencer l'Essai Gratuit de 7 Jours",
        "de": "7 Tage kostenlos testen",
        "it": "Inizia la Prova Gratuita di 7 Giorni",
        "nl": "Start 7 Dagen Gratis Proefperiode",
        "lb": "7 Deeg gratis Test starten"
    },
    "Subscrever Plano Anual": {
        "pt": "Subscrever Plano Anual",
        "en": "Subscribe to Annual Plan",
        "es": "Suscribirse al Plan Anual",
        "fr": "S'abonner au Forfait Annuel",
        "de": "Jahresabo abschließen",
        "it": "Abbonati al Piano Annuale",
        "nl": "Abonneren op Jaarabonnement",
        "lb": "Joresabonnement ofschléissen"
    },
    "Subscrever Plano Mensal": {
        "pt": "Subscrever Plano Mensal",
        "en": "Subscribe to Monthly Plan",
        "es": "Suscribirse al Plan Mensual",
        "fr": "S'abonner au Forfait Mensuel",
        "de": "Monatsabo abschließen",
        "it": "Abbonati al Piano Mensile",
        "nl": "Abonneren op Maandabonnement",
        "lb": "Monatsabonnement ofschléissen"
    },
    "Desbloquear Acesso Vitalício": {
        "pt": "Desbloquear Acesso Vitalício",
        "en": "Unlock Lifetime Access",
        "es": "Desbloquear Acceso Vitalicio",
        "fr": "Débloquer l'Accès à Vie",
        "de": "Lebenslangen Zugang freischalten",
        "it": "Sblocca Accesso a Vita",
        "nl": "Levenslange Toegang Ontgrendelen",
        "lb": "Liewenslaangen Zougang fräischalten"
    },
    "Restaurar Compras": {
        "pt": "Restaurar Compras",
        "en": "Restore Purchases",
        "es": "Restaurar Compras",
        "fr": "Restaurer les Achats",
        "de": "Käufe wiederherstellen",
        "it": "Ripristina Acquisti",
        "nl": "Aankopen Herstellen",
        "lb": "Akeef restauréieren"
    },
    "Termos de Utilização": {
        "pt": "Termos de Utilização",
        "en": "Terms of Use",
        "es": "Términos de Uso",
        "fr": "Conditions d'Utilisation",
        "de": "Nutzungsbedingungen",
        "it": "Termini di Utilizzo",
        "nl": "Gebruiksvoorwaarden",
        "lb": "Notzungsbedéngungen"
    },
    "Política de Privacidade": {
        "pt": "Política de Privacidade",
        "en": "Privacy Policy",
        "es": "Política de Privacidad",
        "fr": "Politique de Confidentialité",
        "de": "Datenschutzrichtlinie",
        "it": "Informativa sulla Privacy",
        "nl": "Privacybeleid",
        "lb": "Dateschutzrichtlinn"
    },
    "Aviso Legal sobre Assinaturas": {
        "pt": "Aviso Legal sobre Assinaturas",
        "en": "Subscription Disclaimer",
        "es": "Aviso Legal sobre Suscripciones",
        "fr": "Avis Légal sur les Abonnements",
        "de": "Abonnement-Hinweis",
        "it": "Note Legali sugli Abbonamenti",
        "nl": "Abonnementsvoorwaarden",
        "lb": "Abonnements-Informatioun"
    },
    "A assinatura renova-se automaticamente a menos que seja cancelada até 24 horas antes do termo do período atual.": {
        "pt": "A assinatura renova-se automaticamente a menos que seja cancelada até 24 horas antes do termo do período atual.",
        "en": "Subscription automatically renews unless canceled at least 24 hours before the end of the current period.",
        "es": "La suscripción se renueva automáticamente a menos que se cancele al menos 24 horas antes del final del período actual.",
        "fr": "L'abonnement se renouvelle automatiquement sauf s'il est annulé au moins 24 heures avant la fin de la période en cours.",
        "de": "Das Abonnement verlängert sich automatisch, sofern es nicht mindestens 24 Stunden vor Ablauf des aktuellen Zeitraums gekündigt wird.",
        "it": "L'abbonamento si rinnova automaticamente a meno che non venga annullato almeno 24 ore prima della fine del periodo corrente.",
        "nl": "Het abonnement wordt automatisch verlengd tenzij het minimaal 24 uur voor het einde van de huidige periode wordt opgezegd.",
        "lb": "D'Abonnement verlängert sech automatesch, ausser et gëtt op d'mannst 24 Stonne virum Enn vun der aktueller Period gekënnegt."
    },
    "Compra concluída com sucesso! Bem-vindo ao VitalPet Premium.": {
        "pt": "Compra concluída com sucesso! Bem-vindo ao VitalPet Premium.",
        "en": "Purchase successful! Welcome to VitalPet Premium.",
        "es": "¡Compra realizada con éxito! Bienvenido a VitalPet Premium.",
        "fr": "Achat réussi ! Bienvenue dans VitalPet Premium.",
        "de": "Kauf erfolgreich! Willkommen bei VitalPet Premium.",
        "it": "Acquisto completato con successo! Benvenuto in VitalPet Premium.",
        "nl": "Aankoop succesvol! Welkom bij VitalPet Premium.",
        "lb": "Akaf erfollegräich! Wëllkomm bei VitalPet Premium."
    },
    "Compras restauradas com sucesso!": {
        "pt": "Compras restauradas com sucesso!",
        "en": "Purchases successfully restored!",
        "es": "¡Compras restauradas con éxito!",
        "fr": "Achats restaurés avec succès !",
        "de": "Käufe erfolgreich wiederhergestellt!",
        "it": "Acquisti ripristinati con successo!",
        "nl": "Aankopen succesvol hersteld!",
        "lb": "Akeef erfollegräich restauréiert!"
    },
    "Nenhuma compra anterior encontrada para restaurar.": {
        "pt": "Nenhuma compra anterior encontrada para restaurar.",
        "en": "No previous purchases found to restore.",
        "es": "No se encontraron compras anteriores para restaurar.",
        "fr": "Aucun achat antérieur trouvé à restaurer.",
        "de": "Keine früheren Käufe zur Wiederherstellung gefunden.",
        "it": "Nessun acquisto precedente trovato da ripristinare.",
        "nl": "Geen eerdere aankopen gevonden om te herstellen.",
        "lb": "Keng fréier Akeef fonnt fir ze restauréieren."
    },
    "VitalPet Premium Ativo": {
        "pt": "VitalPet Premium Ativo",
        "en": "VitalPet Premium Active",
        "es": "VitalPet Premium Activo",
        "fr": "VitalPet Premium Actif",
        "de": "VitalPet Premium Aktiv",
        "it": "VitalPet Premium Attivo",
        "nl": "VitalPet Premium Actief",
        "lb": "VitalPet Premium Aktiv"
    },
    "Plano Ativo": {
        "pt": "Plano Ativo",
        "en": "Active Plan",
        "es": "Plan Activo",
        "fr": "Forfait Actif",
        "de": "Aktiver Plan",
        "it": "Piano Attivo",
        "nl": "Actief Abonnement",
        "lb": "Aktiven Abonnement"
    },
    "Gerir Assinatura": {
        "pt": "Gerir Assinatura",
        "en": "Manage Subscription",
        "es": "Gestionar Suscripción",
        "fr": "Gérer l'Abonnement",
        "de": "Abo verwalten",
        "it": "Gestisci Abbonamento",
        "nl": "Abonnement Beheren",
        "lb": "Abonnement geréieren"
    },
    "Desbloquear Premium": {
        "pt": "Desbloquear Premium",
        "en": "Unlock Premium",
        "es": "Desbloquear Premium",
        "fr": "Débloquer Premium",
        "de": "Premium freischalten",
        "it": "Sblocca Premium",
        "nl": "Ontgrendel Premium",
        "lb": "Premium fräischalten"
    },
    "Obter Premium": {
        "pt": "Obter Premium",
        "en": "Get Premium",
        "es": "Obtener Premium",
        "fr": "Obtenir Premium",
        "de": "Premium holen",
        "it": "Ottieni Premium",
        "nl": "Neem Premium",
        "lb": "Premium kréien"
    },
    "Ver Planos": {
        "pt": "Ver Planos",
        "en": "View Plans",
        "es": "Ver Planes",
        "fr": "Voir les Forfaits",
        "de": "Pläne ansehen",
        "it": "Vedi Piani",
        "nl": "Bekijk Abonnementen",
        "lb": "Pläng kucken"
    },
    "Acesso Vitalício Ativo": {
        "pt": "Acesso Vitalício Ativo",
        "en": "Lifetime Access Active",
        "es": "Acceso Vitalicio Activo",
        "fr": "Accès à Vie Actif",
        "de": "Lebenslanger Zugang Aktiv",
        "it": "Accesso a Vita Attivo",
        "nl": "Levenslange Toegang Actief",
        "lb": "Liewenslaangen Zougang Aktiv"
    },
    "Obrigado por apoiar o desenvolvimento do VitalPet!": {
        "pt": "Obrigado por apoiar o desenvolvimento do VitalPet!",
        "en": "Thank you for supporting VitalPet development!",
        "es": "¡Gracias por apoyar el desarrollo de VitalPet!",
        "fr": "Merci de soutenir le développement de VitalPet !",
        "de": "Vielen Dank für Ihre Unterstützung der VitalPet-Entwicklung!",
        "it": "Grazie per aver supportato lo sviluppo di VitalPet!",
        "nl": "Bedankt voor het steunen van de ontwikkeling van VitalPet!",
        "lb": "Merci fir Är Ënnerstëtzung vun der VitalPet-Entwécklung!"
    }
}

def clean_key_name(text):
    clean = re.sub(r'[^a-zA-Z0-9_]', '_', text.lower())
    clean = re.sub(r'_+', '_', clean).strip('_')
    if clean and clean[0].isdigit():
        clean = "k_" + clean
    return clean

def main():
    print("[*] Updating translations.json...")
    with open(TRANSLATIONS_JSON, "r", encoding="utf-8") as f:
        data = json.load(f)
    
    for k, v in new_translations.items():
        data[k] = v
        
    with open(TRANSLATIONS_JSON, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)
    print(f"[✓] Updated translations.json with {len(new_translations)} keys.")

    print("[*] Updating strings.xml for all 8 languages...")
    for lang, filepath in LANG_FILES.items():
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
                print(f"    [✓] Updated {os.path.basename(os.path.dirname(filepath))}/strings.xml (+{len(new_entries)} entries)")
            else:
                print(f"    [!] Error: </resources> not found in {filepath}")
        else:
            print(f"    [~] No new entries needed for {filepath}")

    print("[*] Done adding subscription translations.")

if __name__ == "__main__":
    main()
