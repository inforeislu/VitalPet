import json
import re

features_translations = {
    # Evolution Photos & Categories
    "Evolução de Peso": {
        "pt": "Evolução de Peso",
        "en": "Weight Evolution",
        "es": "Evolución de Peso",
        "fr": "Évolution du Poids",
        "de": "Gewichtsentwicklung",
        "it": "Evoluzione del Peso",
        "nl": "Gewichtsontwikkeling",
        "lb": "Gewiichtsentwécklung"
    },
    "Cicatrização": {
        "pt": "Cicatrização",
        "en": "Wound Healing",
        "es": "Cicatrización",
        "fr": "Cicatrisation",
        "de": "Wundheilung",
        "it": "Cicatrizzazione",
        "nl": "Wondgenezing",
        "lb": "Wondheelung"
    },
    "Dermatologia / Pele": {
        "pt": "Dermatologia / Pele",
        "en": "Dermatology / Skin",
        "es": "Dermatología / Piel",
        "fr": "Dermatologie / Peau",
        "de": "Dermatologie / Haut",
        "it": "Dermatologia / Pelle",
        "nl": "Dermatologie / Huid",
        "lb": "Dermatologie / Haut"
    },
    "Geral": {
        "pt": "Geral",
        "en": "General",
        "es": "General",
        "fr": "Général",
        "de": "Allgemein",
        "it": "Generale",
        "nl": "Algemeen",
        "lb": "Allgemeng"
    },
    "Fotos de Evolução & Comparador": {
        "pt": "Fotos de Evolução & Comparador",
        "en": "Evolution Photos & Comparator",
        "es": "Fotos de Evolución y Comparador",
        "fr": "Photos d'Évolution & Comparateur",
        "de": "Entwicklungsfotos & Vergleich",
        "it": "Foto di Evoluzione & Comparatore",
        "nl": "Evolutiefoto's & Vergelijker",
        "lb": "Entwécklungsfotoen & Verglach"
    },
    "Adicionar Foto de Evolução": {
        "pt": "Adicionar Foto de Evolução",
        "en": "Add Evolution Photo",
        "es": "Añadir Foto de Evolución",
        "fr": "Ajouter une Photo d'Évolution",
        "de": "Entwicklungsfoto hinzufügen",
        "it": "Aggiungi Foto di Evoluzione",
        "nl": "Evolutiefoto Toevoegen",
        "lb": "Entwécklungsfoto bäisetzen"
    },
    "Registe primeiro um pet para aceder à galeria clínica.": {
        "pt": "Registe primeiro um pet para aceder à galeria clínica.",
        "en": "Register a pet first to access the clinical gallery.",
        "es": "Registre primero una mascota para acceder a la galería clínica.",
        "fr": "Enregistrez d'abord un animal pour accéder à la galerie clinique.",
        "de": "Registrieren Sie zuerst ein Haustier, um auf die klinische Galerie zuzugreifen.",
        "it": "Registra prima un animale per accedere alla galleria clinica.",
        "nl": "Registreer eerst een huisdier om toegang te krijgen tot de klinische galerij.",
        "lb": "Registréiert fir d'éischt en Hausdéier fir Zougang zu der klinescher Galerie ze kréien."
    },
    "Galeria de Fotos": {
        "pt": "Galeria de Fotos",
        "en": "Photo Gallery",
        "es": "Galería de Fotos",
        "fr": "Galerie de Photos",
        "de": "Fotogalerie",
        "it": "Galleria Fotografica",
        "nl": "Fotogalerij",
        "lb": "Fotogalerie"
    },
    "Comparador Antes/Depois": {
        "pt": "Comparador Antes/Depois",
        "en": "Before/After Comparator",
        "es": "Comparador Antes/Después",
        "fr": "Comparateur Avant/Après",
        "de": "Vorher/Nachher-Vergleich",
        "it": "Comparatore Prima/Dopo",
        "nl": "Voor/Na Vergelijker",
        "lb": "Virun/Duerno Verglach"
    },
    "Todas": {
        "pt": "Todas",
        "en": "All",
        "es": "Todas",
        "fr": "Toutes",
        "de": "Alle",
        "it": "Tutte",
        "nl": "Alle",
        "lb": "All"
    },
    "Sem fotos registadas nesta categoria.\nToque no botão abaixo para adicionar a primeira foto.": {
        "pt": "Sem fotos registadas nesta categoria.\nToque no botão abaixo para adicionar a primeira foto.",
        "en": "No photos recorded in this category.\nTap the button below to add the first photo.",
        "es": "No hay fotos registradas en esta categoría.\nToca el botón de abajo para añadir la primera foto.",
        "fr": "Aucune photo enregistrée dans cette catégorie.\nAppuyez sur le bouton ci-dessous pour ajouter la première photo.",
        "de": "Keine Fotos in dieser Kategorie gespeichert.\nTippen Sie auf die Schaltfläche unten, um das erste Foto hinzuzufügen.",
        "it": "Nessuna foto registrata in questa categoria.\nTocca il pulsante qui sotto per aggiungere la prima foto.",
        "nl": "Geen foto's geregistreerd in deze categorie.\nTik op de onderstaande knop om de eerste foto toe te voegen.",
        "lb": "Keng Fotoen an dëser Kategorie registréiert.\nTippt op de Knäppchen ënnen fir déi éischt Foto bäizesetzen."
    },
    "São necessárias pelo menos 2 fotos para usar o comparador antes e depois.": {
        "pt": "São necessárias pelo menos 2 fotos para usar o comparador antes e depois.",
        "en": "At least 2 photos are required to use the before and after comparator.",
        "es": "Se necesitan al menos 2 fotos para usar el comparador antes y después.",
        "fr": "Au moins 2 photos sont nécessaires pour utiliser le comparateur avant et après.",
        "de": "Mindestens 2 Fotos sind erforderlich, um den Vorher-Nachher-Vergleich zu nutzen.",
        "it": "Sono necessarie almeno 2 foto per utilizzare il comparatore prima e dopo.",
        "nl": "Er zijn minstens 2 foto's nodig om de voor-en-na-vergelijker te gebruiken.",
        "lb": "Et si mindestens 2 Fotoen néideg fir de Virun- an Duerno-Vergläicher ze benotzen."
    },
    "Antes: ": {
        "pt": "Antes: ",
        "en": "Before: ",
        "es": "Antes: ",
        "fr": "Avant : ",
        "de": "Vorher: ",
        "it": "Prima: ",
        "nl": "Voor: ",
        "lb": "Virdrun: "
    },
    "Depois: ": {
        "pt": "Depois: ",
        "en": "After: ",
        "es": "Después: ",
        "fr": "Après : ",
        "de": "Nachher: ",
        "it": "Dopo: ",
        "nl": "Na: ",
        "lb": "Duerno: "
    },
    "Arraste a divisória branca lateralmente para comparar o progresso clínico.": {
        "pt": "Arraste a divisória branca lateralmente para comparar o progresso clínico.",
        "en": "Drag the white divider sideways to compare clinical progress.",
        "es": "Arrastra el divisor blanco lateralmente para comparar el progreso clínico.",
        "fr": "Faites glisser le séparateur blanc latéralement pour comparer l'évolution clinique.",
        "de": "Ziehen Sie die weiße Trennlinie seitlich, um den klinischen Fortschritt zu vergleichen.",
        "it": "Trascina il divisore bianco lateralmente per confrontare i progressi clinici.",
        "nl": "Sleep de witte scheidingslijn opzij om de klinische voortgang te vergelijken.",
        "lb": "Zitt déi wäiss Trennlinn säitlech fir de klinesche Fortschrëtt ze vergläichen."
    },

    # PDF Reports
    "Relatório Clínico & Nutricional": {
        "pt": "Relatório Clínico & Nutricional",
        "en": "Clinical & Nutritional Report",
        "es": "Informe Clínico y Nutricional",
        "fr": "Rapport Clinique & Nutritionnel",
        "de": "Klinischer & Ernährungsbericht",
        "it": "Relazione Clinica & Nutrizionale",
        "nl": "Klinisch & Nutritioneel Rapport",
        "lb": "Klineschen & Ernährungsrapport"
    },
    "Data de Emissão: ": {
        "pt": "Data de Emissão: ",
        "en": "Issue Date: ",
        "es": "Fecha de Emisión: ",
        "fr": "Date d'Émission : ",
        "de": "Ausstellungsdatum: ",
        "it": "Data di Emissione: ",
        "nl": "Uitgiftedatum: ",
        "lb": "Ausstellungsdatum: "
    },
    "Identificação do Paciente": {
        "pt": "Identificação do Paciente",
        "en": "Patient Identification",
        "es": "Identificación del Paciente",
        "fr": "Identification du Patient",
        "de": "Patientenidentifikation",
        "it": "Identificazione del Paziente",
        "nl": "Patiëntidentificatie",
        "lb": "Patientenidentifikatioun"
    },
    "Nome: ": {
        "pt": "Nome: ",
        "en": "Name: ",
        "es": "Nombre: ",
        "fr": "Nom : ",
        "de": "Name: ",
        "it": "Nome: ",
        "nl": "Naam: ",
        "lb": "Numm: "
    },
    "Espécie: ": {
        "pt": "Espécie: ",
        "en": "Species: ",
        "es": "Especie: ",
        "fr": "Espèce : ",
        "de": "Art: ",
        "it": "Specie: ",
        "nl": "Soort: ",
        "lb": "Aart: "
    },
    "Sexo: ": {
        "pt": "Sexo: ",
        "en": "Gender: ",
        "es": "Sexo: ",
        "fr": "Sexe : ",
        "de": "Geschlecht: ",
        "it": "Sesso: ",
        "nl": "Geslacht: ",
        "lb": "Geschlecht: "
    },
    "Raça: ": {
        "pt": "Raça: ",
        "en": "Breed: ",
        "es": "Raza: ",
        "fr": "Race : ",
        "de": "Rasse: ",
        "it": "Razza: ",
        "nl": "Ras: ",
        "lb": "Rass: "
    },
    "Idade: ": {
        "pt": "Idade: ",
        "en": "Age: ",
        "es": "Edad: ",
        "fr": "Âge : ",
        "de": "Alter: ",
        "it": "Età: ",
        "nl": "Leeftijd: ",
        "lb": "Alter: "
    },
    "Estado Reprodutivo: ": {
        "pt": "Estado Reprodutivo: ",
        "en": "Reproductive Status: ",
        "es": "Estado Reproductivo: ",
        "fr": "Statut Reproducteur : ",
        "de": "Fortpflanzungsstatus: ",
        "it": "Stato Riproduttivo: ",
        "nl": "Reproductieve Status: ",
        "lb": "Fortplanzungsstatus: "
    },
    "Nível de Atividade: ": {
        "pt": "Nível de Atividade: ",
        "en": "Activity Level: ",
        "es": "Nivel de Actividad: ",
        "fr": "Niveau d'Activité : ",
        "de": "Aktivitätslevel: ",
        "it": "Livello di Attività: ",
        "nl": "Activiteitsniveau: ",
        "lb": "Aktivitéitsniveau: "
    },
    "Microchip: ": {
        "pt": "Microchip: ",
        "en": "Microchip: ",
        "es": "Microchip: ",
        "fr": "Puce électronique : ",
        "de": "Mikrochip: ",
        "it": "Microchip: ",
        "nl": "Microchip: ",
        "lb": "Mikrochip: "
    },
    "Contacto Clínico SOS: ": {
        "pt": "Contacto Clínico SOS: ",
        "en": "Clinical Emergency Contact: ",
        "es": "Contacto Clínico SOS: ",
        "fr": "Contact Clinique SOS : ",
        "de": "Klinischer Notfallkontakt: ",
        "it": "Contatto Clinico SOS: ",
        "nl": "Klinisch Noodcontact: ",
        "lb": "Klineschen Noutfallkontakt: "
    },
    "Biometria & Metabolismo (WSAVA / NRC)": {
        "pt": "Biometria & Metabolismo (WSAVA / NRC)",
        "en": "Biometrics & Metabolism (WSAVA / NRC)",
        "es": "Biometría y Metabolismo (WSAVA / NRC)",
        "fr": "Biométrie & Métabolisme (WSAVA / NRC)",
        "de": "Biometrie & Stoffwechsel (WSAVA / NRC)",
        "it": "Biometria & Metabolismo (WSAVA / NRC)",
        "nl": "Biometrie & Metabolisme (WSAVA / NRC)",
        "lb": "Biometrie & Stoffwiessel (WSAVA / NRC)"
    },
    "Peso Atual: ": {
        "pt": "Peso Atual: ",
        "en": "Current Weight: ",
        "es": "Peso Actual: ",
        "fr": "Poids Actuel : ",
        "de": "Aktuelles Gewicht: ",
        "it": "Peso Attuale: ",
        "nl": "Huidig Gewicht: ",
        "lb": "Aktuellt Gewiicht: "
    },
    "Score Corporal (BCS): ": {
        "pt": "Score Corporal (BCS): ",
        "en": "Body Condition Score (BCS): ",
        "es": "Condición Corporal (BCS): ",
        "fr": "Score Corporel (BCS) : ",
        "de": "Körperzustandswert (BCS): ",
        "it": "Punteggio di Condizione Corporea (BCS): ",
        "nl": "Lichaamsconditiescore (BCS): ",
        "lb": "Kierperkonditiounswäert (BCS): "
    },
    "RER (Taxa Metabólica Basal): ": {
        "pt": "RER (Taxa Metabólica Basal): ",
        "en": "RER (Resting Energy Requirement): ",
        "es": "RER (Requerimiento Energético Basal): ",
        "fr": "RER (Besoin Énergétique de Repos) : ",
        "de": "RER (Ruheenergiebedarf): ",
        "it": "RER (Fabbisogno Energetico a Riposo): ",
        "nl": "RER (Rustenergiebehoefte): ",
        "lb": "RER (Roue-Energiebedarf): "
    },
    "DER (Necessidade Calórica Diária): ": {
        "pt": "DER (Necessidade Calórica Diária): ",
        "en": "DER (Daily Energy Requirement): ",
        "es": "DER (Requerimiento Energético Diario): ",
        "fr": "DER (Besoin Énergétique Quotidien) : ",
        "de": "DER (Tagesenergiebedarf): ",
        "it": "DER (Fabbisogno Energetico Giornaliero): ",
        "nl": "DER (Dagelijkse Energiebehoefte): ",
        "lb": "DER (Dagesenergiebedarf): "
    },
    "Plano Nutricional Prescrito": {
        "pt": "Plano Nutricional Prescrito",
        "en": "Prescribed Nutritional Plan",
        "es": "Plan Nutricional Prescrito",
        "fr": "Plan Nutritionnel Prescrit",
        "de": "Verschriebener Ernährungsplan",
        "it": "Piano Nutrizionale Prescritto",
        "nl": "Voorgeschreven Voedingsplan",
        "lb": "Veruerdente Ernährungsplang"
    },
    "Vacinação & Profilaxia": {
        "pt": "Vacinação & Profilaxia",
        "en": "Vaccination & Prophylaxis",
        "es": "Vacunación y Profilaxis",
        "fr": "Vaccination & Prophylaxie",
        "de": "Impfung & Prophylaxe",
        "it": "Vaccinazione & Profilassi",
        "nl": "Vaccinatie & Profylaxe",
        "lb": "Impfunge & Prophylaxe"
    },
    "Alertas Médicos & Alergias": {
        "pt": "Alertas Médicos & Alergias",
        "en": "Medical Alerts & Allergies",
        "es": "Alertas Médicas y Alergias",
        "fr": "Alertes Médicales & Allergies",
        "de": "Medizinische Warnungen & Allergien",
        "it": "Allarmi Medici & Allergie",
        "nl": "Medische Waarschuwingen & Allergieën",
        "lb": "Medizinesch Warnungen & Allergien"
    },
    "Alergias Identificadas: ": {
        "pt": "Alergias Identificadas: ",
        "en": "Identified Allergies: ",
        "es": "Alergias Identificadas: ",
        "fr": "Allergies Identifiées : ",
        "de": "Erkannte Allergien: ",
        "it": "Allergie Identificate: ",
        "nl": "Geïdentificeerde Allergieën: ",
        "lb": "Identifizéiert Allergien: "
    },
    "Condições Crónicas: ": {
        "pt": "Condições Crónicas: ",
        "en": "Chronic Conditions: ",
        "es": "Condiciones Crónicas: ",
        "fr": "Conditions Chroniques : ",
        "de": "Chronische Erkrankungen: ",
        "it": "Condizioni Croniche: ",
        "nl": "Chronische Aandoeningen: ",
        "lb": "Chronesch Krankheeten: "
    },
    "Partilhar Relatório Clínico (PDF)": {
        "pt": "Partilhar Relatório Clínico (PDF)",
        "en": "Share Clinical Report (PDF)",
        "es": "Compartir Informe Clínico (PDF)",
        "fr": "Partager le Rapport Clinique (PDF)",
        "de": "Klinischen Bericht teilen (PDF)",
        "it": "Condividi Relazione Clinica (PDF)",
        "nl": "Deel Klinisch Rapport (PDF)",
        "lb": "Klinesche Rapport deelen (PDF)"
    },

    # Digital ID & Medal
    "Cartão SOS": {
        "pt": "Cartão SOS",
        "en": "SOS Card",
        "es": "Tarjeta SOS",
        "fr": "Carte SOS",
        "de": "SOS-Karte",
        "it": "Tessera SOS",
        "nl": "SOS-kaart",
        "lb": "SOS-Kaart"
    },
    "Medalha de Coleira": {
        "pt": "Medalha de Coleira",
        "en": "Collar Medal",
        "es": "Medalla de Collar",
        "fr": "Médaille de Collier",
        "de": "Halsband-Medaille",
        "it": "Medaglietta per Collare",
        "nl": "Halsbandpenning",
        "lb": "Halsband-Medail"
    },
    "Se me encontrar, escaneie o código!": {
        "pt": "Se me encontrar, escaneie o código!",
        "en": "If found, please scan the code!",
        "es": "¡Si me encuentras, escanea el código!",
        "fr": "Si vous me trouvez, scannez le code !",
        "de": "Wenn Sie mich finden, scannen Sie den Code!",
        "it": "Se mi trovi, scansiona il codice!",
        "nl": "Als u mij vindt, scan de code!",
        "lb": "Wann Dir mech fannt, scannt de Code!"
    },
    "Exportar / Partilhar Relatório (PDF)": {
        "pt": "Exportar / Partilhar Relatório (PDF)",
        "en": "Export / Share Report (PDF)",
        "es": "Exportar / Compartir Informe (PDF)",
        "fr": "Exporter / Partager le Rapport (PDF)",
        "de": "Bericht exportieren / teilen (PDF)",
        "it": "Esporta / Condividi Relazione (PDF)",
        "nl": "Exporteer / Deel Rapport (PDF)",
        "lb": "Rapport exportéieren / deelen (PDF)"
    },

    # Weight Charts
    "Evolução de Peso & Saúde": {
        "pt": "Evolução de Peso & Saúde",
        "en": "Weight & Health Evolution",
        "es": "Evolución de Peso y Salud",
        "fr": "Évolution du Poids & Santé",
        "de": "Gewichts- & Gesundheitsentwicklung",
        "it": "Evoluzione di Peso & Salute",
        "nl": "Gewichts- & Gezondheidsevolutie",
        "lb": "Gewiichts- & Gesondheetsentwécklung"
    },
    "Registe primeiro um pet para acompanhar a sua evolução clínica.": {
        "pt": "Registe primeiro um pet para acompanhar a sua evolução clínica.",
        "en": "Register a pet first to track clinical evolution.",
        "es": "Registre primero una mascota para seguir su evolución clínica.",
        "fr": "Enregistrez d'abord un animal pour suivre son évolution clinique.",
        "de": "Registrieren Sie zuerst ein Haustier, um dessen klinische Entwicklung zu verfolgen.",
        "it": "Registra prima un animale per monitorare la sua evoluzione clinica.",
        "nl": "Registreer eerst een huisdier om de klinische evolutie te volgen.",
        "lb": "Registréiert fir d'éischt en Hausdéier fir seng klinesch Entwécklung ze verfollegen."
    },
    "Curva de Evolução Ponderal": {
        "pt": "Curva de Evolução Ponderal",
        "en": "Weight Evolution Curve",
        "es": "Curva de Evolución Ponderal",
        "fr": "Courbe d'Évolution du Poids",
        "de": "Gewichtsentwicklungskurve",
        "it": "Curva di Evoluzione Ponderale",
        "nl": "Gewichtsontwikkelingscurve",
        "lb": "Gewiichtsentwécklungskurv"
    },
    "Histórico de Pesagens": {
        "pt": "Histórico de Pesagens",
        "en": "Weight History",
        "es": "Historial de Pesajes",
        "fr": "Historique des Pesées",
        "de": "Wiegehistorie",
        "it": "Storico del Peso",
        "nl": "Weeggeschiedenis",
        "lb": "Gewiichtshistorie"
    },
    "Novo Registo de Peso": {
        "pt": "Novo Registo de Peso",
        "en": "New Weight Entry",
        "es": "Nuevo Registro de Peso",
        "fr": "Nouvelle Pesée",
        "de": "Neuer Gewichtseintrag",
        "it": "Nuovo Registro del Peso",
        "nl": "Nieuwe Gewichtsregistratie",
        "lb": "Neie Gewiichtsantrag"
    },
    "Peso (kg)": {
        "pt": "Peso (kg)",
        "en": "Weight (kg)",
        "es": "Peso (kg)",
        "fr": "Poids (kg)",
        "de": "Gewicht (kg)",
        "it": "Peso (kg)",
        "nl": "Gewicht (kg)",
        "lb": "Gewiicht (kg)"
    },
    "Observações (opcional)": {
        "pt": "Observações (opcional)",
        "en": "Notes (optional)",
        "es": "Observaciones (opcional)",
        "fr": "Remarques (facultatif)",
        "de": "Bemerkungen (optional)",
        "it": "Note (opzionale)",
        "nl": "Opmerkingen (optioneel)",
        "lb": "Bemierkungen (optional)"
    },
    "Registar Peso": {
        "pt": "Registar Peso",
        "en": "Log Weight",
        "es": "Registrar Peso",
        "fr": "Enregistrer le Poids",
        "de": "Gewicht protokollieren",
        "it": "Registra Peso",
        "nl": "Gewicht Registreren",
        "lb": "Gewiicht protokolléieren"
    },
    "Peso Alvo": {
        "pt": "Peso Alvo",
        "en": "Target Weight",
        "es": "Peso Objetivo",
        "fr": "Poids Cible",
        "de": "Zielgewicht",
        "it": "Peso Obiettivo",
        "nl": "Streefgewicht",
        "lb": "Zilgewiicht"
    },
    "Score BCS": {
        "pt": "Score BCS",
        "en": "BCS Score",
        "es": "Puntuación BCS",
        "fr": "Score BCS",
        "de": "BCS-Wert",
        "it": "Punteggio BCS",
        "nl": "BCS-score",
        "lb": "BCS-Score"
    },

    # Breed Encyclopedia & Details
    "Enciclopédia de Raças": {
        "pt": "Enciclopédia de Raças",
        "en": "Breed Encyclopedia",
        "es": "Enciclopedia de Razas",
        "fr": "Encyclopédie des Races",
        "de": "Rasse-Enzyklopädie",
        "it": "Enciclopedia delle Razze",
        "nl": "Rassenencyclopedie",
        "lb": "Rassen-Enzyklopedie"
    },
    "Quiz de Raça": {
        "pt": "Quiz de Raça",
        "en": "Breed Quiz",
        "es": "Quiz de Raza",
        "fr": "Quiz de Race",
        "de": "Rasse-Quiz",
        "it": "Quiz sulle Razze",
        "nl": "Rassenquiz",
        "lb": "Rasse-Quiz"
    },
    "Descobrir compatibilidade": {
        "pt": "Descobrir compatibilidade",
        "en": "Find compatibility",
        "es": "Descubrir compatibilidad",
        "fr": "Découvrir la compatibilité",
        "de": "Kompatibilität entdecken",
        "it": "Scopri la compatibilità",
        "nl": "Compatibiliteit ontdekken",
        "lb": "Kompatibilitéit entdecken"
    },
    "Comparador": {
        "pt": "Comparador",
        "en": "Comparator",
        "es": "Comparador",
        "fr": "Comparateur",
        "de": "Vergleich",
        "it": "Comparatore",
        "nl": "Vergelijker",
        "lb": "Vergläicher"
    },
    "Comparar 2 raças": {
        "pt": "Comparar 2 raças",
        "en": "Compare 2 breeds",
        "es": "Comparar 2 razas",
        "fr": "Comparer 2 races",
        "de": "2 Rassen vergleichen",
        "it": "Confronta 2 razze",
        "nl": "Vergelijk 2 rassen",
        "lb": "2 Rassen vergläichen"
    },
    "Pesquisar por raça, porte ou característica...": {
        "pt": "Pesquisar por raça, porte ou característica...",
        "en": "Search by breed, size, or feature...",
        "es": "Buscar por raza, tamaño o característica...",
        "fr": "Rechercher par race, taille ou caractéristique...",
        "de": "Nach Rasse, Größe oder Merkmal suchen...",
        "it": "Cerca per razza, taglia o caratteristica...",
        "nl": "Zoek op ras, grootte of kenmerk...",
        "lb": "No Rass, Gréisst oder Eegeschaft sichen..."
    },
    "Todas as Espécies": {
        "pt": "Todas as Espécies",
        "en": "All Species",
        "es": "Todas las Especies",
        "fr": "Toutes les Espèces",
        "de": "Alle Tierarten",
        "it": "Tutte le Specie",
        "nl": "Alle Soorten",
        "lb": "All Aarten"
    },
    "Cães": {
        "pt": "Cães",
        "en": "Dogs",
        "es": "Perros",
        "fr": "Chiens",
        "de": "Hunde",
        "it": "Cani",
        "nl": "Honden",
        "lb": "Hënn"
    },
    "Gatos": {
        "pt": "Gatos",
        "en": "Cats",
        "es": "Gatos",
        "fr": "Chats",
        "de": "Katzen",
        "it": "Gatti",
        "nl": "Katten",
        "lb": "Kazen"
    },
    "Todos os Portes": {
        "pt": "Todos os Portes",
        "en": "All Sizes",
        "es": "Todos los Tamaños",
        "fr": "Toutes les Tailles",
        "de": "Alle Größen",
        "it": "Tutte le Taglie",
        "nl": "Alle Groottes",
        "lb": "All Gréissten"
    },
    "raças encontradas": {
        "pt": "raças encontradas",
        "en": "breeds found",
        "es": "razas encontradas",
        "fr": "races trouvées",
        "de": "Rassen gefunden",
        "it": "razze trovate",
        "nl": "rassen gevonden",
        "lb": "Rasse fonnt"
    },
    "Atenção: ": {
        "pt": "Atenção: ",
        "en": "Note: ",
        "es": "Atención: ",
        "fr": "Attention : ",
        "de": "Hinweis: ",
        "it": "Attenzione: ",
        "nl": "Opgelet: ",
        "lb": "Opgepasst: "
    },
    "Limpar": {
        "pt": "Limpar",
        "en": "Clear",
        "es": "Limpiar",
        "fr": "Effacer",
        "de": "Löschen",
        "it": "Cancella",
        "nl": "Wissen",
        "lb": "Läschen"
    },
    "Sobre a Raça": {
        "pt": "Sobre a Raça",
        "en": "About the Breed",
        "es": "Acerca de la Raza",
        "fr": "À propos de la Race",
        "de": "Über die Rasse",
        "it": "Informazioni sulla Razza",
        "nl": "Over het Ras",
        "lb": "Iwwer d'Rass"
    },
    "Predisposições Clínicas & Cuidados": {
        "pt": "Predisposições Clínicas & Cuidados",
        "en": "Clinical Predispositions & Care",
        "es": "Predisposiciones Clínicas y Cuidados",
        "fr": "Prédispositions Cliniques & Soins",
        "de": "Klinische Veranlagungen & Pflege",
        "it": "Predisposizioni Cliniche & Cure",
        "nl": "Klinische Aanleg & Zorg",
        "lb": "Klinesch Dispositiounen & Pfleeg"
    },
    "Esta raça apresenta maior sensibilidade estatística a:": {
        "pt": "Esta raça apresenta maior sensibilidade estatística a:",
        "en": "This breed shows statistically higher sensitivity to:",
        "es": "Esta raza presenta mayor sensibilidad estadística a:",
        "fr": "Cette race présente une sensibilité statistique accrue à :",
        "de": "Diese Rasse weist eine statistisch höhere Empfindlichkeit auf für:",
        "it": "Questa razza presenta una maggiore sensibilità statistica a:",
        "nl": "Dit ras vertoont statistisch een hogere gevoeligheid voor:",
        "lb": "Dës Rass weist eng statistesch méi héich Sensibilitéit op fir:"
    },
    "Cuidados com o Pelo / Escovagem": {
        "pt": "Cuidados com o Pelo / Escovagem",
        "en": "Coat Care / Grooming",
        "es": "Cuidado del Pelaje / Cepillado",
        "fr": "Soin du Pelage / Brossage",
        "de": "Fellpflege / Bürsten",
        "it": "Cura del Pelo / Spazzolatura",
        "nl": "Vachtverzorging / Borstelen",
        "lb": "Pelzfleeg / Biischten"
    },
    "Nível de manutenção do manto e tosquias": {
        "pt": "Nível de manutenção do manto e tosquias",
        "en": "Coat maintenance and trimming requirements",
        "es": "Nivel de mantenimiento del manto y cortes",
        "fr": "Niveau d'entretien du pelage et toilettage",
        "de": "Aufwand für Fellpflege und Scheren",
        "it": "Livello di manutenzione del mantello e tosatura",
        "nl": "Onderhoudsniveau van vacht en trimmen",
        "lb": "Niveau vun der Pelzfleeg a Schéieren"
    },
    "Comparar com Outra Raça": {
        "pt": "Comparar com Outra Raça",
        "en": "Compare with Another Breed",
        "es": "Comparar con Otra Raza",
        "fr": "Comparer avec une Autre Race",
        "de": "Mit einer anderen Rasse vergleichen",
        "it": "Confronta con un'Altra Razza",
        "nl": "Vergelijk met een Ander Ras",
        "lb": "Mat enger anerer Rass vergläichen"
    },
    "Peso Médio": {
        "pt": "Peso Médio",
        "en": "Average Weight",
        "es": "Peso Medio",
        "fr": "Poids Moyen",
        "de": "Durchschnittsgewicht",
        "it": "Peso Medio",
        "nl": "Gemiddeld Gewicht",
        "lb": "Duerchschnëttsgewiicht"
    },
    "Esperança de Vida": {
        "pt": "Esperança de Vida",
        "en": "Life Expectancy",
        "es": "Esperanza de Vida",
        "fr": "Espérance de Vie",
        "de": "Lebenserwartung",
        "it": "Aspettativa di Vita",
        "nl": "Levensverwachting",
        "lb": "Liewenserwaardung"
    },
    "Energia": {
        "pt": "Energia",
        "en": "Energy",
        "es": "Energía",
        "fr": "Énergie",
        "de": "Energie",
        "it": "Energia",
        "nl": "Energie",
        "lb": "Energie"
    },
    "Comparar": {
        "pt": "Comparar",
        "en": "Compare",
        "es": "Comparar",
        "fr": "Comparer",
        "de": "Vergleichen",
        "it": "Confronta",
        "nl": "Vergelijken",
        "lb": "Vergläichen"
    },

    # Breed Comparison
    "Comparador de Raças": {
        "pt": "Comparador de Raças",
        "en": "Breed Comparator",
        "es": "Comparador de Razas",
        "fr": "Comparateur de Races",
        "de": "Rassevergleicher",
        "it": "Comparatore di Razze",
        "nl": "Rassenvergelijker",
        "lb": "Rassevergläicher"
    },
    "Raça A": {
        "pt": "Raça A",
        "en": "Breed A",
        "es": "Raza A",
        "fr": "Race A",
        "de": "Rasse A",
        "it": "Razza A",
        "nl": "Ras A",
        "lb": "Rass A"
    },
    "Raça B": {
        "pt": "Raça B",
        "en": "Breed B",
        "es": "Raza B",
        "fr": "Race B",
        "de": "Rasse B",
        "it": "Razza B",
        "nl": "Ras B",
        "lb": "Rass B"
    },
    "Comparação de Parâmetros": {
        "pt": "Comparação de Parâmetros",
        "en": "Parameter Comparison",
        "es": "Comparación de Parámetros",
        "fr": "Comparaison des Paramètres",
        "de": "Parametervergleich",
        "it": "Confronto dei Parametri",
        "nl": "Parametervergelijking",
        "lb": "Parameterverglach"
    },
    "Porte": {
        "pt": "Porte",
        "en": "Size",
        "es": "Tamaño",
        "fr": "Gabarit",
        "de": "Größe",
        "it": "Taglia",
        "nl": "Grootte",
        "lb": "Gréisst"
    },
    "Nível de Energia": {
        "pt": "Nível de Energia",
        "en": "Energy Level",
        "es": "Nivel de Energía",
        "fr": "Niveau d'Énergie",
        "de": "Energielevel",
        "it": "Livello di Energia",
        "nl": "Energieniveau",
        "lb": "Energieniveau"
    },
    "Escovagem / Cuidados": {
        "pt": "Escovagem / Cuidados",
        "en": "Grooming / Care",
        "es": "Cepillado / Cuidados",
        "fr": "Brossage / Soins",
        "de": "Bürsten / Pflege",
        "it": "Spazzolatura / Cura",
        "nl": "Borstelen / Zorg",
        "lb": "Biischten / Pfleeg"
    },
    "Sensibilidades": {
        "pt": "Sensibilidades",
        "en": "Sensitivities",
        "es": "Sensibilidades",
        "fr": "Sensibilités",
        "de": "Empfindlichkeiten",
        "it": "Sensibilità",
        "nl": "Gevoeligheden",
        "lb": "Sensibilitéiten"
    },
    "Selecionar Raça": {
        "pt": "Selecionar Raça",
        "en": "Select Breed",
        "es": "Seleccionar Raza",
        "fr": "Sélectionner une Race",
        "de": "Rasse auswählen",
        "it": "Seleziona Razza",
        "nl": "Selecteer Ras",
        "lb": "Rass auswielen"
    },
    "Pesquisar...": {
        "pt": "Pesquisar...",
        "en": "Search...",
        "es": "Buscar...",
        "fr": "Rechercher...",
        "de": "Suchen...",
        "it": "Cerca...",
        "nl": "Zoeken...",
        "lb": "Sichen..."
    },
    "Fechar": {
        "pt": "Fechar",
        "en": "Close",
        "es": "Cerrar",
        "fr": "Fermer",
        "de": "Schließen",
        "it": "Chiudi",
        "nl": "Sluiten",
        "lb": "Zoumaachen"
    },

    # Breed Match Quiz
    "Quiz de Raça Ideal": {
        "pt": "Quiz de Raça Ideal",
        "en": "Ideal Breed Quiz",
        "es": "Quiz de Raza Ideal",
        "fr": "Quiz de Race Idéale",
        "de": "Ideales Rasse-Quiz",
        "it": "Quiz sulla Razza Ideale",
        "nl": "Ideale Rassenquiz",
        "lb": "Ideale Rasse-Quiz"
    },
    "Raças Mais Compatíveis!": {
        "pt": "Raças Mais Compatíveis!",
        "en": "Most Compatible Breeds!",
        "es": "¡Razas Más Compatibles!",
        "fr": "Races les Plus Compatibles !",
        "de": "Am besten kompatible Rassen!",
        "it": "Razze Più Compatibili!",
        "nl": "Meest Compatibele Rassen!",
        "lb": "Bescht kompatibel Rassen!"
    },
    "Com base no seu perfil de espaço, tempo e rotina diária:": {
        "pt": "Com base no seu perfil de espaço, tempo e rotina diária:",
        "en": "Based on your space, time, and daily routine profile:",
        "es": "Basado en tu perfil de espacio, tiempo y rutina diaria:",
        "fr": "Selon votre espace, votre temps et votre routine quotidienne :",
        "de": "Basierend auf Ihrem Profil bezüglich Platz, Zeit und Tagesablauf:",
        "it": "In base al tuo spazio, tempo e routine quotidiana:",
        "nl": "Gebaseerd op uw ruimte, tijd en dagelijkse routine:",
        "lb": "Baséierend op Ärem Raum, Ärer Zäit an Ärem Dagesoflaf:"
    },
    "Afinidade": {
        "pt": "Afinidade",
        "en": "Affinity",
        "es": "Afinidad",
        "fr": "Affinité",
        "de": "Affinität",
        "it": "Affinità",
        "nl": "Affiniteit",
        "lb": "Affinitéit"
    },
    "Refazer Quiz": {
        "pt": "Refazer Quiz",
        "en": "Retake Quiz",
        "es": "Repetir Quiz",
        "fr": "Refaire le Quiz",
        "de": "Quiz wiederholen",
        "it": "Rifai il Quiz",
        "nl": "Quiz opnieuw doen",
        "lb": "Quiz nach eng Kéier maachen"
    },
    "PERGUNTA 1 DE 4": {
        "pt": "PERGUNTA 1 DE 4",
        "en": "QUESTION 1 OF 4",
        "es": "PREGUNTA 1 DE 4",
        "fr": "QUESTION 1 SUR 4",
        "de": "FRAGE 1 VON 4",
        "it": "DOMANDA 1 DI 4",
        "nl": "VRAAG 1 VAN 4",
        "lb": "FRO 1 VUN 4"
    },
    "PERGUNTA 2 DE 4": {
        "pt": "PERGUNTA 2 DE 4",
        "en": "QUESTION 2 OF 4",
        "es": "PREGUNTA 2 DE 4",
        "fr": "QUESTION 2 SUR 4",
        "de": "FRAGE 2 VON 4",
        "it": "DOMANDA 2 DI 4",
        "nl": "VRAAG 2 VAN 4",
        "lb": "FRO 2 VUN 4"
    },
    "PERGUNTA 3 DE 4": {
        "pt": "PERGUNTA 3 DE 4",
        "en": "QUESTION 3 OF 4",
        "es": "PREGUNTA 3 DE 4",
        "fr": "QUESTION 3 SUR 4",
        "de": "FRAGE 3 VON 4",
        "it": "DOMANDA 3 DI 4",
        "nl": "VRAAG 3 VAN 4",
        "lb": "FRO 3 VUN 4"
    },
    "PERGUNTA 4 DE 4": {
        "pt": "PERGUNTA 4 DE 4",
        "en": "QUESTION 4 OF 4",
        "es": "PREGUNTA 4 DE 4",
        "fr": "QUESTION 4 SUR 4",
        "de": "FRAGE 4 VON 4",
        "it": "DOMANDA 4 DI 4",
        "nl": "VRAAG 4 VAN 4",
        "lb": "FRO 4 VUN 4"
    },
    "Qual a espécie de companhia desejada?": {
        "pt": "Qual a espécie de companhia desejada?",
        "en": "Which companion species do you prefer?",
        "es": "¿Qué especie de compañía prefieres?",
        "fr": "Quelle espèce de compagnie préférez-vous ?",
        "de": "Welche Tierart bevorzugen Sie als Begleiter?",
        "it": "Quale specie da compagnia desideri?",
        "nl": "Welke gezelschapsdiersoort verkiest u?",
        "lb": "Wéi eng Begleetdéierenaart zitt Dir vir?"
    },
    "O perfil ideal de atividade e cuidados varia consoante a espécie.": {
        "pt": "O perfil ideal de atividade e cuidados varia consoante a espécie.",
        "en": "Activity and care requirements vary depending on the species.",
        "es": "El perfil ideal de actividad y cuidados varía según la especie.",
        "fr": "Le profil idéal d'activité et de soins varie selon l'espèce.",
        "de": "Das ideale Aktivitäts- und Pflegeprofil variiert je nach Art.",
        "it": "Il profilo ideale di attività e cura varia a seconda della specie.",
        "nl": "Het ideale profiel voor activiteit en zorg verschilt per soort.",
        "lb": "Déi ideal Aktivitéits- a Fleegprofiler variéiere jee no Aart."
    },
    "Cão (Canino)": {
        "pt": "Cão (Canino)",
        "en": "Dog (Canine)",
        "es": "Perro (Canino)",
        "fr": "Chien (Canin)",
        "de": "Hund (Canide)",
        "it": "Cane (Canino)",
        "nl": "Hond (Canine)",
        "lb": "Hond (Canin)"
    },
    "Companheiro para passeios diários e treinos ao ar livre": {
        "pt": "Companheiro para passeios diários e treinos ao ar livre",
        "en": "Companion for daily walks and outdoor activities",
        "es": "Compañero para paseos diarios y entrenamientos al aire libre",
        "fr": "Compagnon pour promenades quotidiennes et activités extérieures",
        "de": "Begleiter für tägliche Spaziergänge und Outdoor-Aktivitäten",
        "it": "Compagno per passeggiate quotidiane e attività all'aperto",
        "nl": "Metgezel voor dagelijkse wandelingen en buitenactiviteiten",
        "lb": "Begleeder fir alldeeglech Spadséiergäng an Outdoor-Aktivitéiten"
    },
    "Gato (Felino)": {
        "pt": "Gato (Felino)",
        "en": "Cat (Feline)",
        "es": "Gato (Felino)",
        "fr": "Chat (Félin)",
        "de": "Katze (Katzenartig)",
        "it": "Gatto (Felino)",
        "nl": "Kat (Feline)",
        "lb": "Kaz (Felin)"
    },
    "Companheiro independente e adaptável para viver no interior": {
        "pt": "Companheiro independente e adaptável para viver no interior",
        "en": "Independent and adaptable companion for indoor living",
        "es": "Compañero independiente y adaptable para vivir en el interior",
        "fr": "Compagnon indépendant et adaptable pour la vie en intérieur",
        "de": "Unabhängiger und anpassungsfähiger Begleiter für die Wohnung",
        "it": "Compagno indipendente e adattabile per la vita in casa",
        "nl": "Onafhankelijke en flexibele metgezel voor binnenshuis leven",
        "lb": "Onofhängege an adaptiven Begleeder fir d'Wunneng"
    },
    "Como é o seu espaço habitacional?": {
        "pt": "Como é o seu espaço habitacional?",
        "en": "What is your living space like?",
        "es": "¿Cómo es tu espacio habitacional?",
        "fr": "Comment est votre espace de vie ?",
        "de": "Wie ist Ihre Wohnsituation?",
        "it": "Com'è il tuo spazio abitativo?",
        "nl": "Hoe ziet uw woonruimte eruit?",
        "lb": "Wéi ass Är Wunnsituatioun?"
    },
    "O porte e nível de energia do pet devem harmonizar com o seu lar.": {
        "pt": "O porte e nível de energia do pet devem harmonizar com o seu lar.",
        "en": "The pet's size and energy level should fit harmoniously with your home.",
        "es": "El tamaño y nivel de energía deben armonizar con tu hogar.",
        "fr": "La taille et l'énergie de l'animal doivent correspondre à votre domicile.",
        "de": "Größe und Energielevel sollten harmonisch zu Ihrem Zuhause passen.",
        "it": "Taglia ed energia dell'animale devono armonizzarsi con la tua casa.",
        "nl": "De grootte en het energieniveau van het dier moeten passen bij uw woning.",
        "lb": "D'Gréisst an d'Energieniveau vum Déier sollten harmonesch zu Ärem Heem passen."
    },
    "Apartamento Pequeno": {
        "pt": "Apartamento Pequeno",
        "en": "Small Apartment",
        "es": "Apartamento Pequeño",
        "fr": "Petit Appartement",
        "de": "Kleine Wohnung",
        "it": "Appartamento Piccolo",
        "nl": "Klein Appartement",
        "lb": "Klengt Appartement"
    },
    "Espaço mais compacto sem área exterior privativa": {
        "pt": "Espaço mais compacto sem área exterior privativa",
        "en": "Compact space without private outdoor area",
        "es": "Espacio más compacto sin zona exterior privada",
        "fr": "Espace compact sans espace extérieur privé",
        "de": "Kompakter Raum ohne privaten Außenbereich",
        "it": "Spazio compatto senza area esterna privata",
        "nl": "Compacte ruimte zonder eigen buitenruimte",
        "lb": "Kompakte Raum ouni privaten Outdoorberäich"
    },
    "Apartamento Médio / Grande": {
        "pt": "Apartamento Médio / Grande",
        "en": "Medium / Large Apartment",
        "es": "Apartamento Mediano / Grande",
        "fr": "Appartement Moyen / Grand",
        "de": "Mittlere / Große Wohnung",
        "it": "Appartamento Medio / Grande",
        "nl": "Middelgroot / Groot Appartement",
        "lb": "Mëttel / Grousst Appartement"
    },
    "Espaço confortável com varanda ou áreas amplas": {
        "pt": "Espaço confortável com varanda ou áreas amplas",
        "en": "Comfortable space with balcony or spacious rooms",
        "es": "Espacio cómodo con balcón o áreas amplias",
        "fr": "Espace confortable avec balcon ou pièces spacieuses",
        "de": "Komfortabler Raum mit Balkon oder geräumigen Zimmern",
        "it": "Spazio confortevole con balcone o stanze ampie",
        "nl": "Comfortabele ruimte met balkon of ruime kamers",
        "lb": "Komfortabele Raum mat Balcon oder grousse Zëmmeren"
    },
    "Moradia com Quintal / Jardim": {
        "pt": "Moradia com Quintal / Jardim",
        "en": "House with Yard / Garden",
        "es": "Casa con Patio / Jardín",
        "fr": "Maison avec Cour / Jardin",
        "de": "Haus mit Hof / Garten",
        "it": "Casa con Cortile / Giardino",
        "nl": "Woning met Tuin / Binnenplaats",
        "lb": "Haus mat Haff / Gaart"
    },
    "Espaço amplo com área exterior vedada para correr": {
        "pt": "Espaço amplo com área exterior vedada para correr",
        "en": "Spacious home with fenced outdoor area to run",
        "es": "Espacio amplio con zona exterior vallada para correr",
        "fr": "Maison spacieuse avec espace extérieur clôturé pour courir",
        "de": "Großzügiges Areal mit umzäuntem Außenbereich zum Laufen",
        "it": "Spazio ampio con area esterna recintata per correre",
        "nl": "Ruime woning met omheinde buitenruimte om te rennen",
        "lb": "Grousse Raum mat ëmzäuntem Outdoorberäich fir ze lafen"
    },
    "Quanto tempo diário tem para passeios e brincadeiras?": {
        "pt": "Quanto tempo diário tem para passeios e brincadeiras?",
        "en": "How much daily time do you have for walks and play?",
        "es": "¿Cuánto tiempo diario tienes para paseos y juegos?",
        "fr": "De combien de temps quotidien disposez-vous pour les promenades et les jeux ?",
        "de": "Wie viel Zeit haben Sie täglich für Spaziergänge und Spiele?",
        "it": "Quanto tempo quotidiano hai per passeggiate e giochi?",
        "nl": "Hoeveel dagelijkse tijd heeft u voor wandelingen en spelen?",
        "lb": "Wéi vill alldeeglech Zäit hutt Dir fir Spadséiergäng a Spiller?"
    },
    "Pets com alta energia necessitam de estímulo motor para evitar ansiedade.": {
        "pt": "Pets com alta energia necessitam de estímulo motor para evitar ansiedade.",
        "en": "High-energy pets require physical stimulation to prevent anxiety.",
        "es": "Las mascotas de alta energía necesitan estímulo motor para evitar ansiedad.",
        "fr": "Les animaux très énergiques ont besoin d'exercice pour éviter l'anxiété.",
        "de": "Hochenergetische Tiere brauchen Bewegung, um Angstzustände zu vermeiden.",
        "it": "Gli animali molto energici necessitano di stimoli motori per evitare ansia.",
        "nl": "Energierijke dieren hebben lichaamsbeweging nodig om angst te voorkomen.",
        "lb": "Déiere mat héijer Energie brauche Bewegung fir Angscht ze vermeiden."
    },
    "Menos de 30 minutos / dia": {
        "pt": "Menos de 30 minutos / dia",
        "en": "Less than 30 minutes / day",
        "es": "Menos de 30 minutos / día",
        "fr": "Moins de 30 minutes / jour",
        "de": "Weniger als 30 Minuten / Tag",
        "it": "Meno di 30 minuti / giorno",
        "nl": "Minder dan 30 minuten / dag",
        "lb": "Manner wéi 30 Minutten / Dag"
    },
    "Passeios rápidos higiénicos e rotina calma em casa": {
        "pt": "Passeios rápidos higiénicos e rotina calma em casa",
        "en": "Quick toilet walks and calm indoor routine",
        "es": "Paseos rápidos higiénicos y rutina tranquila en casa",
        "fr": "Sorties rapides d'hygiène et routine calme à la maison",
        "de": "Kurze Gassirunden und ruhiger Ablauf zu Hause",
        "it": "Uscite rapide igieniche e routine tranquilla a casa",
        "nl": "Korte toiletuitjes en rustige routine binnenshuis",
        "lb": "Kuerz Spadséiergäng a rouegen Oflaf doheem"
    },
    "30 a 60 minutos / dia": {
        "pt": "30 a 60 minutos / dia",
        "en": "30 to 60 minutes / day",
        "es": "30 a 60 minutos / día",
        "fr": "30 à 60 minutes / jour",
        "de": "30 bis 60 Minuten / Tag",
        "it": "Da 30 a 60 minuti / giorno",
        "nl": "30 tot 60 minuten / dag",
        "lb": "30 bis 60 Minutten / Dag"
    },
    "Caminhadas diárias ativas e brincadeiras regulares": {
        "pt": "Caminhadas diárias ativas e brincadeiras regulares",
        "en": "Active daily walks and regular playtime",
        "es": "Caminatas diarias activas y juegos regulares",
        "fr": "Promenades quotidiennes actives et jeux réguliers",
        "de": "Aktive tägliche Spaziergänge und regelmäßiges Spielen",
        "it": "Passeggiate quotidiane attive e gioco regolare",
        "nl": "Actieve dagelijkse wandelingen en regelmatig spelen",
        "lb": "Aktiv alldeeglech Spadséiergäng a reegelméissegt Spillen"
    },
    "Mais de 1 hora / dia": {
        "pt": "Mais de 1 hora / dia",
        "en": "More than 1 hour / day",
        "es": "Más de 1 hora / día",
        "fr": "Plus d'une heure / jour",
        "de": "Mehr als 1 Stunde / Tag",
        "it": "Più di 1 ora / giorno",
        "nl": "Meer dan 1 uur / dag",
        "lb": "Méi wéi 1 Stonn / Dag"
    },
    "Corrida, trilhos ou treino físico intenso diário": {
        "pt": "Corrida, trilhos ou treino físico intenso diário",
        "en": "Running, trails, or intensive daily workout",
        "es": "Correr, senderos o entrenamiento físico diario intenso",
        "fr": "Course, randonnée ou entraînement physique quotidien intensif",
        "de": "Laufen, Trails oder intensives tägliches Training",
        "it": "Corsa, percorsi o allenamento fisico quotidiano intenso",
        "nl": "Hardlopen, trektochten of intensieve dagelijkse training",
        "lb": "Lafen, Trëppeltier oder intensivt deeglecht Training"
    },
    "Qual a sua tolerância a escovagem e cuidados com o pelo?": {
        "pt": "Qual a sua tolerância a escovagem e cuidados com o pelo?",
        "en": "What is your tolerance for brushing and coat care?",
        "es": "¿Cuál es tu tolerancia al cepillado y cuidado del pelaje?",
        "fr": "Quelle est votre tolérance pour le brossage et les soins du pelage ?",
        "de": "Wie viel Zeit möchten Sie in Bürsten und Fellpflege investieren?",
        "it": "Qual è la tua disponibilità per la spazzolatura e la cura del pelo?",
        "nl": "Hoeveel tijd wilt u besteden aan borstelen en vachtverzorging?",
        "lb": "Wéi vill Zäit wëllt Dir a Biischten a Pelzfleeg investéieren?"
    },
    "Algumas raças exigem escovagens diárias e tosquias regulares.": {
        "pt": "Algumas raças exigem escovagens diárias e tosquias regulares.",
        "en": "Some breeds require daily brushing and regular grooming.",
        "es": "Algunas razas requieren cepillado diario y peluquería regular.",
        "fr": "Certaines races nécessitent un brossage quotidien et un toilettage régulier.",
        "de": "Manche Rassen erfordern tägliches Bürsten und regelmäßiges Scheren.",
        "it": "Alcune razze richiedono spazzolatura quotidiana e toelettatura regolare.",
        "nl": "Sommige rassen vereisen dagelijks borstelen en regelmatig trimmen.",
        "lb": "E puer Rassen erfuerderen deeglecht Biischten a reegelméissegt Schéieren."
    },
    "Pouca manutenção": {
        "pt": "Pouca manutenção",
        "en": "Low maintenance",
        "es": "Bajo mantenimiento",
        "fr": "Peu d'entretien",
        "de": "Geringer Aufwand",
        "it": "Bassa manutenzione",
        "nl": "Weinig onderhoud",
        "lb": "Wéineg Ënnerhalt"
    },
    "Prefiro pelo curto e escovagens rápidas e raras": {
        "pt": "Prefiro pelo curto e escovagens rápidas e raras",
        "en": "I prefer short coat and quick, occasional brushing",
        "es": "Prefiero pelo corto y cepillados rápidos y ocasionales",
        "fr": "Je préfère les poils courts et un brossage rapide et occasionnel",
        "de": "Ich bevorzuge kurzes Fell und seltenes, schnelles Bürsten",
        "it": "Preferisco pelo corto e spazzolature rapide e occasionali",
        "nl": "Ik geef de voorkeur aan een korte vacht en af en toe snel borstelen",
        "lb": "Ech hu kuerze Pelz a seelen, séier Biischte léiwer"
    },
    "Manutenção moderada": {
        "pt": "Manutenção moderada",
        "en": "Moderate maintenance",
        "es": "Mantenimiento moderado",
        "fr": "Entretien modéré",
        "de": "Mäßiger Aufwand",
        "it": "Manutenzione moderata",
        "nl": "Matig onderhoud",
        "lb": "Mëttelméissegen Ënnerhalt"
    },
    "Posso escovar 1 a 2 vezes por semana sem problema": {
        "pt": "Posso escovar 1 a 2 vezes por semana sem problema",
        "en": "I can brush 1 to 2 times a week without any problem",
        "es": "Puedo cepillar 1 o 2 veces por semana sin problema",
        "fr": "Je peux brosser 1 à 2 fois par semaine sans problème",
        "de": "Ich kann problemlos 1 bis 2 Mal pro Woche bürsten",
        "it": "Posso spazzolare 1 o 2 volte a settimana senza problemi",
        "nl": "Ik kan probleemloos 1 tot 2 keer per week borstelen",
        "lb": "Ech ka problemlos 1 bis 2 Mol d'Woch biischten"
    },
    "Dedicação alta / Tosquias": {
        "pt": "Dedicação alta / Tosquias",
        "en": "High dedication / Grooming",
        "es": "Alta dedicación / Peluquería",
        "fr": "Forte implication / Toilettage",
        "de": "Hoher Aufwand / Scheren",
        "it": "Alta dedizione / Tosatura",
        "nl": "Hoge toewijding / Trimmen",
        "lb": "Héijen Asaz / Schéieren"
    },
    "Gosto de cuidar do pelo e ir ao pet shop regularmente": {
        "pt": "Gosto de cuidar do pelo e ir ao pet shop regularmente",
        "en": "I enjoy grooming and visiting the salon regularly",
        "es": "Me gusta cuidar el pelaje e ir a la peluquería regularmente",
        "fr": "J'aime prendre soin du pelage et aller au salon régulièrement",
        "de": "Ich pflege gerne das Fell und besuche regelmäßig den Salon",
        "it": "Mi piace curare il pelo e andare regolarmente al salone",
        "nl": "Ik vind vachtverzorging leuk en ga regelmatig naar de trimsalon",
        "lb": "Ech këmmeren mech gär ëm de Pelz a ginn dacks an de Salon"
    },
    "Anterior": {
        "pt": "Anterior",
        "en": "Previous",
        "es": "Anterior",
        "fr": "Précédent",
        "de": "Zurück",
        "it": "Precedente",
        "nl": "Vorige",
        "lb": "Virdrun"
    },
    "Seguinte": {
        "pt": "Seguinte",
        "en": "Next",
        "es": "Siguiente",
        "fr": "Suivant",
        "de": "Weiter",
        "it": "Avanti",
        "nl": "Volgende",
        "lb": "Weider"
    },
    "Ver Resultados": {
        "pt": "Ver Resultados",
        "en": "View Results",
        "es": "Ver Resultados",
        "fr": "Voir les Résultats",
        "de": "Ergebnisse anzeigen",
        "it": "Visualizza Risultati",
        "nl": "Resultaten Bekijken",
        "lb": "Resultater kucken"
    },

    # Symptom Journal
    "Diário Clínico de Sintomas": {
        "pt": "Diário Clínico de Sintomas",
        "en": "Clinical Symptom Journal",
        "es": "Diario Clínico de Síntomas",
        "fr": "Journal Clinique des Symptômes",
        "de": "Klinisches Symptomtagebuch",
        "it": "Diario Clinico dei Sintomi",
        "nl": "Klinisch Symptoomdagboek",
        "lb": "Klinescht Symptomdagbuch"
    },
    "Registar Sintoma": {
        "pt": "Registar Sintoma",
        "en": "Log Symptom",
        "es": "Registrar Síntoma",
        "fr": "Enregistrer un Symptôme",
        "de": "Symptom protokollieren",
        "it": "Registra Sintomo",
        "nl": "Symptoom Registreren",
        "lb": "Symptom protokolléieren"
    },
    "Registe primeiro um pet para aceder ao diário de sintomas.": {
        "pt": "Registe primeiro um pet para aceder ao diário de sintomas.",
        "en": "Register a pet first to access the symptom journal.",
        "es": "Registre primero una mascota para acceder al diario de síntomas.",
        "fr": "Enregistrez d'abord un animal pour accéder au journal des symptômes.",
        "de": "Registrieren Sie zuerst ein Haustier, um auf das Symptomtagebuch zuzugreifen.",
        "it": "Registra prima un animale per accedere al diario dei sintomi.",
        "nl": "Registreer eerst een huisdier om toegang te krijgen tot het symptoomdagboek.",
        "lb": "Registréiert fir d'éischt en Hausdéier fir Zougang zum Symptomdagbuch ze kréien."
    },
    "Alerta Digestivo dos Últimos 7 Dias": {
        "pt": "Alerta Digestivo dos Últimos 7 Dias",
        "en": "7-Day Digestive Alert",
        "es": "Alerta Digestiva de los Últimos 7 Días",
        "fr": "Alerte Digestive des 7 Derniers Jours",
        "de": "Verdauungswarnung der letzten 7 Tage",
        "it": "Allarme Digestivo degli Ultimi 7 Giorni",
        "nl": "Verteringswaarschuwing laatste 7 dagen",
        "lb": "Verdauungswarnung vun de leschte 7 Deeg"
    },
    "• Fezes muito moles ou líquidas (Score 6-7) nos últimos 3 dias.": {
        "pt": "• Fezes muito moles ou líquidas (Score 6-7) nos últimos 3 dias.",
        "en": "• Very soft or watery stool (Score 6-7) in the last 3 days.",
        "es": "• Heces muy blandas o líquidas (Score 6-7) en los últimos 3 días.",
        "fr": "• Selles très molles ou liquides (Score 6-7) au cours des 3 derniers jours.",
        "de": "• Sehr weicher oder wässriger Stuhl (Score 6-7) in den letzten 3 Tagen.",
        "it": "• Feci molto molli o liquide (Punteggio 6-7) negli ultimi 3 giorni.",
        "nl": "• Zeer zachte of waterige ontlasting (Score 6-7) in de afgelopen 3 dagen.",
        "lb": "• Ganz mëllen oder wässerege Stull (Score 6-7) an de leschten 3 Deeg."
    },
    "episódio(s) de vómito registados recentemente.": {
        "pt": "episódio(s) de vómito registados recentemente.",
        "en": "vomiting episode(s) recorded recently.",
        "es": "episodio(s) de vómito registrados recientemente.",
        "fr": "épisode(s) de vomissement enregistré(s) récemment.",
        "de": "Erbrechensepisode(n) kürzlich erfasst.",
        "it": "episodio/i di vomito registrato/i di recente.",
        "nl": "braakepisose(s) recent geregistreerd.",
        "lb": "Erbriechungsepisod(en) viru kuerzem registréiert."
    },
    "Histórico de Registos": {
        "pt": "Histórico de Registos",
        "en": "Log History",
        "es": "Historial de Registros",
        "fr": "Historique des Saisies",
        "de": "Eintragshistorie",
        "it": "Cronologia Registrazioni",
        "nl": "Geschiedenis van Inzendingen",
        "lb": "Enträgshistorie"
    },
    "registos": {
        "pt": "registos",
        "en": "logs",
        "es": "registros",
        "fr": "enregistrements",
        "de": "Einträge",
        "it": "registri",
        "nl": "invoeren",
        "lb": "Enträg"
    },
    "Nenhum sintoma digestivo ou clínico registado.\nToque no botão + para adicionar um novo registo.": {
        "pt": "Nenhum sintoma digestivo ou clínico registado.\nToque no botão + para adicionar um novo registo.",
        "en": "No digestive or clinical symptoms logged.\nTap the + button to add a new record.",
        "es": "No hay síntomas digestivos o clínicos registrados.\nToca el botón + para añadir un nuevo registro.",
        "fr": "Aucun symptôme digestif ou clinique enregistré.\nAppuyez sur le bouton + pour ajouter une nouvelle entrée.",
        "de": "Keine verdauungsbezogenen oder klinischen Symptome erfasst.\nTippen Sie auf +, um einen Eintrag hinzuzufügen.",
        "it": "Nessun sintomo digestivo o clinico registrato.\nTocca il pulsante + per aggiungere una voce.",
        "nl": "Geen spijsverterings- of klinische symptomen geregistreerd.\nTik op + om een item toe te voegen.",
        "lb": "Keng Verdauungs- oder klinesch Symptomer erfaasst.\nTippt op + fir en neien Eintrag bäizesetzen."
    },
    "Score Fecal (Purina/Bristol)": {
        "pt": "Score Fecal (Purina/Bristol)",
        "en": "Fecal Score (Purina/Bristol)",
        "es": "Puntuación Fecal (Purina/Bristol)",
        "fr": "Score Fécal (Purina/Bristol)",
        "de": "Fäkalien-Score (Purina/Bristol)",
        "it": "Punteggio Fecale (Purina/Bristol)",
        "nl": "Fecale Score (Purina/Bristol)",
        "lb": "Kot-Score (Purina/Bristol)"
    },
    "Muito duro e seco": {
        "pt": "Muito duro e seco",
        "en": "Very hard and dry",
        "es": "Muy duro y seco",
        "fr": "Très dur et sec",
        "de": "Sehr hart und trocken",
        "it": "Molto duro e secco",
        "nl": "Zeer hard en droog",
        "lb": "Ganz haart an dréchen"
    },
    "Ideal, firme e moldado": {
        "pt": "Ideal, firme e moldado",
        "en": "Ideal, firm and shaped",
        "es": "Ideal, firme y formado",
        "fr": "Idéal, ferme et moulé",
        "de": "Ideal, fest und geformt",
        "it": "Ideale, solido e formato",
        "nl": "Ideaal, stevig en gevormd",
        "lb": "Ideal, fest a geformt"
    },
    "Húmido, mas retém forma": {
        "pt": "Húmido, mas retém forma",
        "en": "Moist, but retains shape",
        "es": "Húmedo, pero mantiene la forma",
        "fr": "Humide, mais garde sa forme",
        "de": "Feucht, behält aber die Form",
        "it": "Umido, ma mantiene la forma",
        "nl": "Vochtig, maar behoudt vorm",
        "lb": "Fiicht, behält awer d'Form"
    },
    "Muito mole com forma perdida": {
        "pt": "Muito mole com forma perdida",
        "en": "Very soft, loses shape",
        "es": "Muy blando, pierde la forma",
        "fr": "Très mou, perd sa forme",
        "de": "Sehr weich, formlos",
        "it": "Molto molle, informe",
        "nl": "Zeer zacht, verliest vorm",
        "lb": "Ganz mëll, ouni Form"
    },
    "Viscoso e pastoso": {
        "pt": "Viscoso e pastoso",
        "en": "Viscous and pasty",
        "es": "Viscoso y pastoso",
        "fr": "Visqueux et pâteux",
        "de": "Viskos und breiig",
        "it": "Viscido e pastoso",
        "nl": "Slijmerig en pasteus",
        "lb": "Schläimeg a breiegt"
    },
    "Textura líquida com pedaços": {
        "pt": "Textura líquida com pedaços",
        "en": "Liquid texture with pieces",
        "es": "Textura líquida con trozos",
        "fr": "Texture liquide avec morceaux",
        "de": "Flüssig mit Klümpchen",
        "it": "Liquido con pezzi",
        "nl": "Vloeibaar met stukjes",
        "lb": "Flësseg mat Klompen"
    },
    "Diarreia totalmente líquida": {
        "pt": "Diarreia totalmente líquida",
        "en": "Entirely watery diarrhea",
        "es": "Diarrea totalmente líquida",
        "fr": "Diarrhée totalement liquide",
        "de": "Vollständig wässriger Durchfall",
        "it": "Diarrea completamente acquosa",
        "nl": "Volledig waterige diarree",
        "lb": "Ganz wässeregen Duerchfall"
    },
    "Episódios de Vómito": {
        "pt": "Episódios de Vómito",
        "en": "Vomiting Episodes",
        "es": "Episodios de Vómito",
        "fr": "Épisodes de Vomissement",
        "de": "Erbrechensepisoden",
        "it": "Episodi di Vomito",
        "nl": "Braakepisoden",
        "lb": "Erbriechungsepisoden"
    },
    "Observações Clínicas (opcional)": {
        "pt": "Observações Clínicas (opcional)",
        "en": "Clinical Observations (optional)",
        "es": "Observaciones Clínicas (opcional)",
        "fr": "Observations Cliniques (facultatif)",
        "de": "Klinische Beobachtungen (optional)",
        "it": "Osservazioni Cliniche (opzionale)",
        "nl": "Klinische Waarnemingen (optioneel)",
        "lb": "Klinesch Observatiounen (optional)"
    },
    "Novo Registo de Sintomas": {
        "pt": "Novo Registo de Sintomas",
        "en": "New Symptom Log",
        "es": "Nuevo Registro de Síntomas",
        "fr": "Nouvel Enregistrement de Symptômes",
        "de": "Neues Symptomprotokoll",
        "it": "Nuova Registrazione Sintomi",
        "nl": "Nieuw Symptomenlogboek",
        "lb": "Neit Symptomprotokoll"
    },

    # Custom Recipes & Hub sections
    "Nova Receita": {
        "pt": "Nova Receita",
        "en": "New Recipe",
        "es": "Nueva Receta",
        "fr": "Nouvelle Recette",
        "de": "Neues Rezept",
        "it": "Nuova Ricetta",
        "nl": "Nieuw Recept",
        "lb": "Neit Rezept"
    },
    "Própria": {
        "pt": "Própria",
        "en": "Custom",
        "es": "Propia",
        "fr": "Maison",
        "de": "Eigenes",
        "it": "Personale",
        "nl": "Eigen",
        "lb": "Eegent"
    },
    "Nova Receita Personalizada": {
        "pt": "Nova Receita Personalizada",
        "en": "New Custom Recipe",
        "es": "Nueva Receta Personalizada",
        "fr": "Nouvelle Recette Personnalisée",
        "de": "Neues individuelles Rezept",
        "it": "Nuova Ricetta Personalizzata",
        "nl": "Nieuw Aangepast Recept",
        "lb": "Neit personaliséiert Rezept"
    },
    "Título da Receita": {
        "pt": "Título da Receita",
        "en": "Recipe Title",
        "es": "Título de la Receta",
        "fr": "Titre de la Recette",
        "de": "Rezepttitel",
        "it": "Titolo della Ricetta",
        "nl": "Recepttitel",
        "lb": "Rezepttitel"
    },
    "Categoria (ex: Dieta Cozida)": {
        "pt": "Categoria (ex: Dieta Cozida)",
        "en": "Category (e.g. Cooked Diet)",
        "es": "Categoría (ej: Dieta Cocida)",
        "fr": "Catégorie (ex : Repas Cuit)",
        "de": "Kategorie (z.B. Kochfutter)",
        "it": "Categoria (es: Dieta Cotta)",
        "nl": "Categorie (bijv. Gekookt Voer)",
        "lb": "Kategorie (z.B. Gekacht Iessen)"
    },
    "Indicações Clínicas": {
        "pt": "Indicações Clínicas",
        "en": "Clinical Indications",
        "es": "Indicaciones Clínicas",
        "fr": "Indications Cliniques",
        "de": "Klinische Indikationen",
        "it": "Indicazioni Cliniche",
        "nl": "Klinische Indicaties",
        "lb": "Klinesch Indikatiounen"
    },
    "Ingredientes (1 por linha)": {
        "pt": "Ingredientes (1 por linha)",
        "en": "Ingredients (1 per line)",
        "es": "Ingredientes (1 por línea)",
        "fr": "Ingrédients (1 par ligne)",
        "de": "Zutaten (1 pro Zeile)",
        "it": "Ingredienti (1 per riga)",
        "nl": "Ingrediënten (1 per regel)",
        "lb": "Zutaten (1 pro Zeil)"
    },
    "Instruções de Preparo": {
        "pt": "Instruções de Preparo",
        "en": "Preparation Instructions",
        "es": "Instrucciones de Preparación",
        "fr": "Instructions de Préparation",
        "de": "Zubereitungshinweise",
        "it": "Istruzioni di Preparazione",
        "nl": "Bereidingsinstructies",
        "lb": "Zoubereedungsinstruktiounen"
    },
    "Receita Caseira": {
        "pt": "Receita Caseira",
        "en": "Homemade Recipe",
        "es": "Receta Casera",
        "fr": "Recette Maison",
        "de": "Hausgemachtes Rezept",
        "it": "Ricetta Casalinga",
        "nl": "Zelfgemaakt Recept",
        "lb": "Hausgemaacht Rezept"
    },
    "Receita personalizada registada pelo tutor.": {
        "pt": "Receita personalizada registada pelo tutor.",
        "en": "Custom recipe logged by the pet owner.",
        "es": "Receta personalizada registrada por el tutor.",
        "fr": "Recette personnalisée enregistrée par le propriétaire.",
        "de": "Individuelles Rezept vom Halter eingetragen.",
        "it": "Ricetta personalizzata registrata dal proprietario.",
        "nl": "Aangepast recept geregistreerd door de eigenaar.",
        "lb": "Personaliséiert Rezept vum Halter registréiert."
    },
    "Sem instruções específicas.": {
        "pt": "Sem instruções específicas.",
        "en": "No specific instructions.",
        "es": "Sin instrucciones específicas.",
        "fr": "Pas d'instructions spécifiques.",
        "de": "Keine spezifischen Anweisungen.",
        "it": "Nessuna istruzione specifica.",
        "nl": "Geen specifieke instructies.",
        "lb": "Keng spezifesch Instruktiounen."
    },
    "Acompanhamento Clínico & Evolução": {
        "pt": "Acompanhamento Clínico & Evolução",
        "en": "Clinical Monitoring & Evolution",
        "es": "Seguimiento Clínico y Evolución",
        "fr": "Suivi Clinique & Évolution",
        "de": "Klinische Überwachung & Entwicklung",
        "it": "Monitoraggio Clinico & Evoluzione",
        "nl": "Klinische Monitoring & Evolutie",
        "lb": "Klinesch Iwwerwaachung & Entwécklung"
    },
    "Evolução de Peso & Gráficos": {
        "pt": "Evolução de Peso & Gráficos",
        "en": "Weight Evolution & Charts",
        "es": "Evolución de Peso y Gráficos",
        "fr": "Évolution du Poids & Graphiques",
        "de": "Gewichtsentwicklung & Diagramme",
        "it": "Evoluzione di Peso & Grafici",
        "nl": "Gewichtsevolutie & Grafieken",
        "lb": "Gewiichtsentwécklung & Grafiken"
    },
    "Curva ponderal interativa com linha de peso alvo e metas": {
        "pt": "Curva ponderal interativa com linha de peso alvo e metas",
        "en": "Interactive weight curve with target line and goals",
        "es": "Curva ponderal interactiva con línea de peso objetivo y metas",
        "fr": "Courbe de poids interactive avec ligne cible et objectifs",
        "de": "Interaktive Gewichtskurve mit Zielgewichtslinie und Zielen",
        "it": "Curva di peso interattiva con linea del peso obiettivo e traguardi",
        "nl": "Interactieve gewichtscurve met streefgewichtlijn en doelen",
        "lb": "Interaktiv Gewiichtskurv mat Zilgewiichts优先 an Ziler"
    },
    "Fotos & Comparador Antes/Depois": {
        "pt": "Fotos & Comparador Antes/Depois",
        "en": "Photos & Before/After Comparator",
        "es": "Fotos y Comparador Antes/Después",
        "fr": "Photos & Comparateur Avant/Après",
        "de": "Fotos & Vorher/Nachher-Vergleich",
        "it": "Foto & Comparatore Prima/Dopo",
        "nl": "Foto's & Voor/Na Vergelijker",
        "lb": "Fotoen & Virun/Duerno Verglach"
    },
    "Galeria clínica com slider de cicatrização e perda de peso": {
        "pt": "Galeria clínica com slider de cicatrização e perda de peso",
        "en": "Clinical gallery with slider for healing and weight loss",
        "es": "Galería clínica con deslizador de cicatrización y pérdida de peso",
        "fr": "Galerie clinique avec curseur pour cicatrisation et perte de poids",
        "de": "Klinische Galerie mit Schieberegler für Wundheilung und Gewichtsverlust",
        "it": "Galleria clinica con cursore per guarigione e perdita di peso",
        "nl": "Klinische galerij met schuifregelaar voor genezing en gewichtsverlies",
        "lb": "Klinesch Galerie mat Schieber fir Wondheelung a Gewiichtsverloscht"
    },
    "Registo digestivo com escala Bristol/Purina e alertas": {
        "pt": "Registo digestivo com escala Bristol/Purina e alertas",
        "en": "Digestive log with Bristol/Purina scale and alerts",
        "es": "Registro digestivo con escala Bristol/Purina y alertas",
        "fr": "Journal digestif avec échelle Bristol/Purina et alertes",
        "de": "Verdauungsprotokoll mit Bristol/Purina-Skala und Warnungen",
        "it": "Registro digestivo con scala Bristol/Purina e avvisi",
        "nl": "Spijsverteringslogboek met Bristol/Purina-schaal en waarschuwingen",
        "lb": "Verdauungsprotokoll mat Bristol/Purina-Skala a Warnungen"
    },
    "Rotina & Enciclopédia de Raças": {
        "pt": "Rotina & Enciclopédia de Raças",
        "en": "Routine & Breed Encyclopedia",
        "es": "Rutina y Enciclopedia de Razas",
        "fr": "Routine & Encyclopédie des Races",
        "de": "Routine & Rasse-Enzyklopädie",
        "it": "Routine & Enciclopedia delle Razze",
        "nl": "Routine & Rassenencyclopedie",
        "lb": "Routine & Rassen-Enzyklopedie"
    },
    "Catálogo completo com filtros de porte, energia e sensibilidades": {
        "pt": "Catálogo completo com filtros de porte, energia e sensibilidades",
        "en": "Complete catalog with size, energy, and sensitivity filters",
        "es": "Catálogo completo con filtros de tamaño, energía y sensibilidades",
        "fr": "Catalogue complet avec filtres de taille, énergie et sensibilités",
        "de": "Vollständiger Katalog mit Filtern für Größe, Energie und Empfindlichkeiten",
        "it": "Catalogo completo con filtri per taglia, energia e sensibilità",
        "nl": "Volledige catalogus met filters voor grootte, energie en gevoeligheden",
        "lb": "Komplette Katalog mat Filtere fir Gréisst, Energie a Sensibilitéiten"
    },
    "Descubra a raça ideal para o seu estilo de vida e habitação": {
        "pt": "Descubra a raça ideal para o seu estilo de vida e habitação",
        "en": "Discover the ideal breed for your lifestyle and living space",
        "es": "Descubre la raza ideal para tu estilo de vida y vivienda",
        "fr": "Découvrez la race idéale pour votre style de vie et votre logement",
        "de": "Entdecken Sie die ideale Rasse für Ihren Lebensstil und Wohnraum",
        "it": "Scopri la razza ideale per il tuo stile di vita e abitazione",
        "nl": "Ontdek het ideale ras voor uw levensstijl en woonsituatie",
        "lb": "Entdeckt déi ideal Rass fir Äre Liewensstil a Wunnraum"
    },
    "Comparação lado a lado de parâmetros de duas raças": {
        "pt": "Comparação lado a lado de parâmetros de duas raças",
        "en": "Side-by-side comparison of parameters for two breeds",
        "es": "Comparación lado a lado de parámetros de dos razas",
        "fr": "Comparaison côte à côte des paramètres de deux races",
        "de": "Direkter Vergleich der Parameter zweier Rassen",
        "it": "Confronto affiancato dei parametri di due razze",
        "nl": "Vergelijking van parameters van twee rassen naast elkaar",
        "lb": "Säit-u-Säit Verglach vun de Parameteren vun zwou Rassen"
    },

    # PetDetailScreen Quick Buttons
    "Gráficos": {
        "pt": "Gráficos",
        "en": "Charts",
        "es": "Gráficos",
        "fr": "Graphiques",
        "de": "Diagramme",
        "it": "Grafici",
        "nl": "Grafieken",
        "lb": "Grafiken"
    },
    "Fotos": {
        "pt": "Fotos",
        "en": "Photos",
        "es": "Fotos",
        "fr": "Photos",
        "de": "Fotos",
        "it": "Foto",
        "nl": "Foto's",
        "lb": "Fotoen"
    },
    "Sintomas": {
        "pt": "Sintomas",
        "en": "Symptoms",
        "es": "Síntomas",
        "fr": "Symptômes",
        "de": "Symptome",
        "it": "Sintomi",
        "nl": "Symptomen",
        "lb": "Symptomer"
    },
    "Ver Gráfico": {
        "pt": "Ver Gráfico",
        "en": "View Chart",
        "es": "Ver Gráfico",
        "fr": "Voir le Graphique",
        "de": "Diagramm ansehen",
        "it": "Visualizza Grafico",
        "nl": "Grafiek Bekijken",
        "lb": "Grafik kucken"
    },
    "Ver Diário": {
        "pt": "Ver Diário",
        "en": "View Journal",
        "es": "Ver Diario",
        "fr": "Voir le Journal",
        "de": "Tagebuch ansehen",
        "it": "Visualizza Diario",
        "nl": "Dagboek Bekijken",
        "lb": "Dagbuch kucken"
    },
    "Partilhar Texto": {
        "pt": "Partilhar Texto",
        "en": "Share Text",
        "es": "Compartir Texto",
        "fr": "Partager le Texte",
        "de": "Text teilen",
        "it": "Condividi Testo",
        "nl": "Deel Tekst",
        "lb": "Text deelen"
    },
    "Sem sintomas clínicos registados recentemente.": {
        "pt": "Sem sintomas clínicos registados recentemente.",
        "en": "No clinical symptoms logged recently.",
        "es": "No hay síntomas clínicos registrados recientemente.",
        "fr": "Aucun symptôme clinique enregistré récemment.",
        "de": "Kürzlich keine klinischen Symptome erfasst.",
        "it": "Nessun sintomo clinico registrato di recente.",
        "nl": "Geen klinische symptomen recent geregistreerd.",
        "lb": "Keng klinesch Symptomer viru kuerzem registréiert."
    }
}

# 1. Update translations.json
with open("app/src/main/assets/translations.json", "r", encoding="utf-8") as f:
    translations = json.load(f)

for k, v in features_translations.items():
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
    for k, trans in features_translations.items():
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

print(f"Added {len(features_translations)} new parity translation keys across all 8 languages successfully.")
