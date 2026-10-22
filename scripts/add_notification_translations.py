import json
import re

notification_translations = {
    "Jejum Cirúrgico e Pré-Operatório": {
        "pt": "Jejum Cirúrgico e Pré-Operatório",
        "en": "Surgical and Preoperative Fasting",
        "es": "Ayuno Quirúrgico y Preoperatorio",
        "fr": "Jeûne Chirurgical et Préopératoire",
        "de": "Chirurgisches und Präoperatives Fasten",
        "it": "Digiuno Chirurgico e Preoperatorio",
        "nl": "Chirurgisch en Preoperatief Vasten",
        "lb": "Chirurgescht a Preoperatiivt Faaschten"
    },
    "Alertas de corte de sólidos, água e prontidão para cirurgia.": {
        "pt": "Alertas de corte de sólidos, água e prontidão para cirurgia.",
        "en": "Alerts for solid food, water cutoff, and surgery readiness.",
        "es": "Alertas de corte de sólidos, agua y preparación para cirugía.",
        "fr": "Alertes pour l'arrêt des solides, de l'eau et préparation chirurgicale.",
        "de": "Warnungen für Festnahrungs-, Wasserstopp und Operationsbereitschaft.",
        "it": "Avvisi per il blocco dei cibi solidi, dell'acqua e prontezza per la chirurgia.",
        "nl": "Meldingen voor vaste voeding, waterstop en operatiegereedheid.",
        "lb": "Warnungen fir fest Nahrung, Waasserstopp an Operatiounsbereetschaft."
    },
    "Lembretes de Medicação": {
        "pt": "Lembretes de Medicação",
        "en": "Medication Reminders",
        "es": "Recordatorios de Medicación",
        "fr": "Rappels de Médication",
        "de": "Medikamenten-Erinnerungen",
        "it": "Promemoria Farmaci",
        "nl": "Medicatieherinneringen",
        "lb": "Medikamenter-Erënnerungen"
    },
    "Horários de administração de fármacos e tratamentos.": {
        "pt": "Horários de administração de fármacos e tratamentos.",
        "en": "Schedules for administering drugs and treatments.",
        "es": "Horarios de administración de fármacos y tratamientos.",
        "fr": "Horaires d'administration des médicaments et traitements.",
        "de": "Zeitpläne für die Verabreichung von Arzneimitteln und Behandlungen.",
        "it": "Orari di somministrazione di farmaci e trattamenti.",
        "nl": "Schema's voor toediening van medicijnen en behandelingen.",
        "lb": "Zäitpläng fir d'Verabreechung vu Medikamenter a Behandlungen."
    },
    "Acompanhamento de Peso": {
        "pt": "Acompanhamento de Peso",
        "en": "Weight Tracking",
        "es": "Seguimiento de Peso",
        "fr": "Suivi du Poids",
        "de": "Gewichtsüberwachung",
        "it": "Monitoraggio del Peso",
        "nl": "Gewichtsopvolging",
        "lb": "Gewiichtsverfolgung"
    },
    "Lembretes periódicos de pesagem e condição corporal.": {
        "pt": "Lembretes periódicos de pesagem e condição corporal.",
        "en": "Periodic reminders for weighing and body condition.",
        "es": "Recordatorios periódicos de pesaje y condición corporal.",
        "fr": "Rappels périodiques de pesée et de condition corporelle.",
        "de": "Regelmäßige Erinnerungen an Wiegen und Körperzustand.",
        "it": "Promemoria periodici di pesatura e condizione corporea.",
        "nl": "Periodieke herinneringen voor wegen en lichaamsconditie.",
        "lb": "Reegelméisseg Erënnerungen u Weien a Kierperzoustand."
    },
    "Alertas Gerais Clínicos": {
        "pt": "Alertas Gerais Clínicos",
        "en": "General Clinical Alerts",
        "es": "Alertas Generales Clínicas",
        "fr": "Alertes Cliniques Générales",
        "de": "Allgemeine Klinische Warnungen",
        "it": "Avvisi Clinici Generali",
        "nl": "Algemene Klinische Meldingen",
        "lb": "Allgemeng Klinesch Warnungen"
    },
    "Avisos de vacinação, desparasitação e consultas.": {
        "pt": "Avisos de vacinação, desparasitação e consultas.",
        "en": "Notices for vaccination, deworming, and appointments.",
        "es": "Avisos de vacunación, desparasitación y consultas.",
        "fr": "Avis de vaccination, vermifugation et consultations.",
        "de": "Hinweise zu Impfungen, Entwurmungen und Terminen.",
        "it": "Avvisi di vaccinazione, sverminazione e visite.",
        "nl": "Berichten voor vaccinatie, ontworming en consultaties.",
        "lb": "Notizen iwwer Impfungen, Entwurmung a Visitten."
    },
    "VitalPet: Início do Jejum de Sólidos": {
        "pt": "VitalPet: Início do Jejum de Sólidos",
        "en": "VitalPet: Start of Solid Food Fasting",
        "es": "VitalPet: Inicio del Ayuno de Sólidos",
        "fr": "VitalPet: Début du Jeûne de Solides",
        "de": "VitalPet: Beginn des Festnahrungsfastens",
        "it": "VitalPet: Inizio del Digiuno da Solidi",
        "nl": "VitalPet: Begin van Vaste Voeding Vasten",
        "lb": "VitalPet: Ufank vum Festnahrungsfaaschten"
    },
    "Retire toda a alimentação sólida de %s agora. Apenas água fresca é permitida.": {
        "pt": "Retire toda a alimentação sólida de %s agora. Apenas água fresca é permitida.",
        "en": "Remove all solid food from %s now. Only fresh water is allowed.",
        "es": "Retire todo el alimento sólido de %s ahora. Solo se permite agua fresca.",
        "fr": "Retirez toute nourriture solide de %s maintenant. Seule de l'eau fraîche est autorisée.",
        "de": "Entfernen Sie jetzt jedes feste Futter von %s. Nur frisches Wasser ist erlaubt.",
        "it": "Rimuovi tutto il cibo solido di %s adesso. È consentita solo acqua fresca.",
        "nl": "Verwijder al het vaste voedsel van %s nu. Alleen vers water is toegestaan.",
        "lb": "Huelt all fest Iessen vu %s elo ewech. Nëmme frëscht Waasser ass erlaabt."
    },
    "VitalPet: Jejum Total Obrigatório": {
        "pt": "VitalPet: Jejum Total Obrigatório",
        "en": "VitalPet: Mandatory Total Fasting",
        "es": "VitalPet: Ayuno Total Obligatorio",
        "fr": "VitalPet: Jeûne Total Obligatoire",
        "de": "VitalPet: Obligatorisches Totales Fasten",
        "it": "VitalPet: Digiuno Totale Obbligatorio",
        "nl": "VitalPet: Verplicht Totaal Vasten",
        "lb": "VitalPet: Obligatorescht Totalfaaschten"
    },
    "Retire a água de %s agora. O animal deve permanecer em jejum absoluto até ao procedimento.": {
        "pt": "Retire a água de %s agora. O animal deve permanecer em jejum absoluto até ao procedimento.",
        "en": "Remove water from %s now. The animal must remain in total fasting until the procedure.",
        "es": "Retire el agua de %s ahora. El animal debe permanecer en ayuno absoluto hasta el procedimiento.",
        "fr": "Retirez l'eau de %s maintenant. L'animal doit rester à jeun complet jusqu'à la procédure.",
        "de": "Entfernen Sie jetzt das Wasser von %s. Das Tier muss bis zum Eingriff absolut nüchtern bleiben.",
        "it": "Rimuovi l'acqua di %s adesso. L'animale deve rimanere a digiuno assoluto fino alla procedura.",
        "nl": "Verwijder water van %s nu. Het dier moet volledig nuchter blijven tot de ingreep.",
        "lb": "Huelt d'Waasser vu %s elo ewech. D'Déier muss komplett nuchter bleiwen bis zum Agrëff."
    },
    "VitalPet: Hora do Procedimento Clínico": {
        "pt": "VitalPet: Hora do Procedimento Clínico",
        "en": "VitalPet: Clinical Procedure Time",
        "es": "VitalPet: Hora del Procedimiento Clínico",
        "fr": "VitalPet: Heure de la Procédure Clinique",
        "de": "VitalPet: Zeit des Klinischen Eingriffs",
        "it": "VitalPet: Ora della Procedura Clinica",
        "nl": "VitalPet: Tijd van Klinische Ingreep",
        "lb": "VitalPet: Zäit vum Klineschen Agrëff"
    },
    "%s concluiu o protocolo de jejum com sucesso e está pronto(a) para %s.": {
        "pt": "%s concluiu o protocolo de jejum com sucesso e está pronto(a) para %s.",
        "en": "%s successfully completed the fasting protocol and is ready for %s.",
        "es": "%s completó el protocolo de ayuno con éxito y está listo(a) para %s.",
        "fr": "%s a terminé le protocole de jeûne avec succès et est prêt(e) pour %s.",
        "de": "%s hat das Fastenprotokoll erfolgreich abgeschlossen und ist bereit für %s.",
        "it": "%s ha completato il protocollo di digiuno con successo ed è pronto(a) per %s.",
        "nl": "%s heeft het vastenprotocol met succes voltooid en is klaar voor %s.",
        "lb": "%s huet de Faaschtenprotokoll erfollegräich ofgeschloss an ass prett fir %s."
    },
    "VitalPet: Hora da Pesagem de Controlo": {
        "pt": "VitalPet: Hora da Pesagem de Controlo",
        "en": "VitalPet: Routine Weigh-In Time",
        "es": "VitalPet: Hora del Pesaje de Control",
        "fr": "VitalPet: Heure de la Pesée de Contrôle",
        "de": "VitalPet: Zeit für Kontrollwägung",
        "it": "VitalPet: Ora del Controllo del Peso",
        "nl": "VitalPet: Tijd voor Controleweging",
        "lb": "VitalPet: Zäit fir d'Kontrollweien"
    },
    "Já passaram %d dias. Registe o peso atual de %s para acompanhar a curva nutricional.": {
        "pt": "Já passaram %d dias. Registe o peso atual de %s para acompanhar a curva nutricional.",
        "en": "%d days have passed. Record %s's current weight to monitor nutritional progress.",
        "es": "Han pasado %d días. Registre el peso actual de %s para seguir la curva nutricional.",
        "fr": "%d jours se sont écoulés. Enregistrez le poids actuel de %s pour suivre la courbe nutritionnelle.",
        "de": "%d Tage sind vergangen. Erfassen Sie das aktuelle Gewicht von %s für den Ernährungsverlauf.",
        "it": "Sono passati %d giorni. Registra il peso attuale di %s per monitorare la curva nutrizionale.",
        "nl": "Er zijn %d dagen verstreken. Noteer het huidige gewicht van %s om de voedingscurve te volgen.",
        "lb": "Et sinn %d Deeg vergaangen. Registréiert d'aktuellt Gewiicht vu %s fir d'Ernährungskurv."
    },
    "VitalPet: Notificação de Teste": {
        "pt": "VitalPet: Notificação de Teste",
        "en": "VitalPet: Test Notification",
        "es": "VitalPet: Notificación de Prueba",
        "fr": "VitalPet: Notification de Test",
        "de": "VitalPet: Testbenachrichtigung",
        "it": "VitalPet: Notifica di Prova",
        "nl": "VitalPet: Testmelding",
        "lb": "VitalPet: Testnotifikatioun"
    },
    "O sistema de alertas clínicos e notificações está ativo e configurado com sucesso.": {
        "pt": "O sistema de alertas clínicos e notificações está ativo e configurado com sucesso.",
        "en": "The clinical alerts and notifications system is active and successfully configured.",
        "es": "El sistema de alertas clínicas y notificaciones está activo y configurado con éxito.",
        "fr": "Le système d'alertes cliniques et de notifications est actif et configuré avec succès.",
        "de": "Das klinische Warn- und Benachrichtigungssystem ist aktiv und erfolgreich konfiguriert.",
        "it": "Il sistema di avvisi clinici e notifiche è attivo e configurato con successo.",
        "nl": "Het systeem voor klinische meldingen is actief en succesvol geconfigureerd.",
        "lb": "De klinesche Warn- an Notifikatiounssystem ass aktiv an erfollegräich konfiguréiert."
    },
    "Selecionar Animal:": {
        "pt": "Selecionar Animal:",
        "en": "Select Pet:",
        "es": "Seleccionar Mascota:",
        "fr": "Sélectionner l'Animal:",
        "de": "Tier Auswählen:",
        "it": "Seleziona Animale:",
        "nl": "Selecteer Huisdier:",
        "lb": "Déier Auswielen:"
    },
    "Jejum Ativo": {
        "pt": "Jejum Ativo",
        "en": "Active Fasting",
        "es": "Ayuno Activo",
        "fr": "Jeûne Actif",
        "de": "Aktives Fasten",
        "it": "Digiuno Attivo",
        "nl": "Actief Vasten",
        "lb": "Aktiivt Faaschten"
    },
    "Alimentação Permitida": {
        "pt": "Alimentação Permitida",
        "en": "Feeding Allowed",
        "es": "Alimentación Permitida",
        "fr": "Alimentation Autorisée",
        "de": "Fütterung Erlaubt",
        "it": "Alimentazione Consentita",
        "nl": "Voeden Toegestaan",
        "lb": "Iessen Erlaabt"
    },
    "O animal pode alimentar-se normalmente até ao início da janela de jejum.": {
        "pt": "O animal pode alimentar-se normalmente até ao início da janela de jejum.",
        "en": "The pet can feed normally until the fasting window begins.",
        "es": "La mascota puede alimentarse con normalidad hasta el inicio del ayuno.",
        "fr": "L'animal peut se nourrir normalement jusqu'au début de la période de jeûne.",
        "de": "Das Tier kann normal gefüttert werden bis das Fastenfenster beginnt.",
        "it": "L'animale può alimentarsi normalmente fino all'inizio della finestra di digiuno.",
        "nl": "Het huisdier kan normaal eten tot het vastenvenster begint.",
        "lb": "D'Déier ka normal iessen bis d'Faaschtefënster ufänkt."
    },
    "Jejum de Sólidos (Água Permitida)": {
        "pt": "Jejum de Sólidos (Água Permitida)",
        "en": "Solid Food Fasting (Water Allowed)",
        "es": "Ayuno de Sólidos (Agua Permitida)",
        "fr": "Jeûne de Solides (Eau Autorisée)",
        "de": "Festnahrungsfasten (Wasser Erlaubt)",
        "it": "Digiuno da Solidi (Acqua Consentita)",
        "nl": "Vaste Voeding Vasten (Water Toegestaan)",
        "lb": "Festnahrungsfaaschten (Waasser Erlaabt)"
    },
    "Retire toda a ração, petiscos e sólidos. Mantenha apenas água fresca disponível.": {
        "pt": "Retire toda a ração, petiscos e sólidos. Mantenha apenas água fresca disponível.",
        "en": "Remove all kibble, treats, and solids. Keep only fresh water available.",
        "es": "Retire todo el pienso, premios y sólidos. Mantenga solo agua fresca disponible.",
        "fr": "Retirez toutes les croquettes, friandises et solides. Gardez uniquement de l'eau fraîche.",
        "de": "Entfernen Sie Trockenfutter, Leckerlis und Festes. Nur frisches Wasser bereitstellen.",
        "it": "Rimuovi croccantini, snack e cibi solidi. Lascia solo acqua fresca disponibile.",
        "nl": "Verwijder alle brokjes, snacks en vaste voeding. Houd alleen vers water beschikbaar.",
        "lb": "Huelt all Dréchefudder, Séissegkeeten a Festes ewech. Nëmme frëscht Waasser ubidden."
    },
    "Retire imediatamente a taça da água. O animal não pode ingerir nada até à cirurgia.": {
        "pt": "Retire imediatamente a taça da água. O animal não pode ingerir nada até à cirurgia.",
        "en": "Remove water bowl immediately. The pet cannot ingest anything until surgery.",
        "es": "Retire inmediatamente el cuenco de agua. La mascota no puede ingerir nada hasta la cirugía.",
        "fr": "Retirez immédiatement le bol d'eau. L'animal ne peut rien ingérer jusqu'à la chirurgie.",
        "de": "Entfernen Sie sofort den Wassernapf. Das Tier darf bis zur OP nichts zu sich nehmen.",
        "it": "Rimuovi immediatamente la ciotola dell'acqua. L'animale non può ingerire nulla fino all'intervento.",
        "nl": "Verwijder direct de waterbak. Het huisdier mag niets innemen tot de operatie.",
        "lb": "Huelt direkt d'Waasserschossel ewech. D'Déier däerf näischt anhuelen bis d'Operatioun."
    },
    "Pronto para Cirurgia / Sedação": {
        "pt": "Pronto para Cirurgia / Sedação",
        "en": "Ready for Surgery / Sedation",
        "es": "Listo para Cirugía / Sedación",
        "fr": "Prêt pour Chirurgie / Sédation",
        "de": "Bereit für OP / Sedierung",
        "it": "Pronto per Chirurgia / Sedazione",
        "nl": "Klaar voor Operatie / Sedatie",
        "lb": "Prett fir Operatioun / Sedéierung"
    },
    "O tempo de esvaziamento gástrico foi atingido com segurança para indução anestésica.": {
        "pt": "O tempo de esvaziamento gástrico foi atingido com segurança para indução anestésica.",
        "en": "Gastric emptying time safely reached for anesthetic induction.",
        "es": "Tiempo de vaciado gástrico alcanzado de forma segura para inducción anestésica.",
        "fr": "Temps de vidange gastrique atteint en toute sécurité pour induction anesthésique.",
        "de": "Magensaftentleerungszeit für Narkoseeinleitung sicher erreicht.",
        "it": "Tempo di svuotamento gastrico raggiunto in sicurezza per l'induzione anestetica.",
        "nl": "Maagledigingstijd veilig bereikt voor anesthetische inductie.",
        "lb": "Mooleerungszäit sécher erreecht fir Anästhesieinduktioun."
    },
    "Procedimento Concluído": {
        "pt": "Procedimento Concluído",
        "en": "Procedure Completed",
        "es": "Procedimiento Concluido",
        "fr": "Procédure Terminée",
        "de": "Eingriff Abgeschlossen",
        "it": "Procedura Conclusa",
        "nl": "Ingreep Voltooid",
        "lb": "Agrëff Ofgeschloss"
    },
    "O protocolo foi concluído.": {
        "pt": "O protocolo foi concluído.",
        "en": "The protocol has been completed.",
        "es": "El protocolo ha concluido.",
        "fr": "Le protocole a été terminé.",
        "de": "Das Protokoll wurde abgeschlossen.",
        "it": "Il protocollo è stato completato.",
        "nl": "Het protocol is voltooid.",
        "lb": "De Protokoll gouf ofgeschloss."
    },
    "Para o Procedimento": {
        "pt": "Para o Procedimento",
        "en": "Until Procedure",
        "es": "Para el Procedimiento",
        "fr": "Avant la Procédure",
        "de": "Bis zum Eingriff",
        "it": "Alla Procedura",
        "nl": "Tot de Ingreep",
        "lb": "Bis zum Agrëff"
    },
    "Pronto!": {
        "pt": "Pronto!",
        "en": "Ready!",
        "es": "¡Listo!",
        "fr": "Prêt !",
        "de": "Bereit!",
        "it": "Pronto!",
        "nl": "Klaar!",
        "lb": "Prett!"
    },
    "Etapas Fisiológicas do Jejum:": {
        "pt": "Etapas Fisiológicas do Jejum:",
        "en": "Physiological Fasting Stages:",
        "es": "Etapas Fisiológicas del Ayuno:",
        "fr": "Étapes Physiologiques du Jeûne :",
        "de": "Physiologische Fastenstufen:",
        "it": "Fasi Fisiologiche del Digiuno:",
        "nl": "Fysiologische Vastenfasen:",
        "lb": "Physiologesch Faaschtenstufen:"
    },
    "Corte de Sólidos (Sem Ração/Comida)": {
        "pt": "Corte de Sólidos (Sem Ração/Comida)",
        "en": "Solid Food Cutoff (No Food/Kibble)",
        "es": "Corte de Sólidos (Sin Pienso/Comida)",
        "fr": "Arrêt des Solides (Sans Croquettes/Nourriture)",
        "de": "Stopp von Festnahrung (Kein Futter)",
        "it": "Taglio dei Solidi (Niente Cibo/Crocchette)",
        "nl": "Stop Vaste Voeding (Geen Brokken/Eten)",
        "lb": "Festnahrungsstopp (Kee Fudder)"
    },
    "Corte de Líquidos (Retirar Água)": {
        "pt": "Corte de Líquidos (Retirar Água)",
        "en": "Liquids Cutoff (Withdraw Water)",
        "es": "Corte de Líquidos (Retirar Agua)",
        "fr": "Arrêt des Liquides (Retirer l'Eau)",
        "de": "Flüssigkeitsstopp (Wasser Entfernen)",
        "it": "Taglio dei Liquidi (Togliere Acqua)",
        "nl": "Vloeistofstop (Water Wegnemen)",
        "lb": "Flëssegkeetsstopp (Waasser Ewechhuelen)"
    },
    "Hora do Procedimento Veterinário": {
        "pt": "Hora do Procedimento Veterinário",
        "en": "Veterinary Procedure Time",
        "es": "Hora del Procedimiento Veterinario",
        "fr": "Heure de la Procédure Vétérinaire",
        "de": "Zeit des Tierärztlichen Eingriffs",
        "it": "Ora della Procedura Veterinaria",
        "nl": "Tijd van Dierenartsingreep",
        "lb": "Zäit vum Veterinär Agrëff"
    },
    "Novo Protocolo de Jejum para %s": {
        "pt": "Novo Protocolo de Jejum para %s",
        "en": "New Fasting Protocol for %s",
        "es": "Nuevo Protocolo de Ayuno para %s",
        "fr": "Nouveau Protocole de Jeûne pour %s",
        "de": "Neues Fastenprotokoll für %s",
        "it": "Nuovo Protocollo di Digiuno per %s",
        "nl": "Nieuw Vastenprotocol voor %s",
        "lb": "Neie Faaschtenprotokoll fir %s"
    },
    "Procedimento Clínico:": {
        "pt": "Procedimento Clínico:",
        "en": "Clinical Procedure:",
        "es": "Procedimiento Clínico:",
        "fr": "Procédure Clinique :",
        "de": "Klinischer Eingriff:",
        "it": "Procedura Clinica:",
        "nl": "Klinische Ingreep:",
        "lb": "Klineschen Agrëff:"
    },
    "Nome do Procedimento": {
        "pt": "Nome do Procedimento",
        "en": "Procedure Name",
        "es": "Nombre del Procedimiento",
        "fr": "Nom de la Procédure",
        "de": "Name des Eingriffs",
        "it": "Nome della Procedura",
        "nl": "Naam van de Ingreep",
        "lb": "Numm vum Agrëff"
    },
    "Início do Procedimento:": {
        "pt": "Início do Procedimento:",
        "en": "Procedure Start:",
        "es": "Inicio del Procedimiento:",
        "fr": "Début de la Procédure :",
        "de": "Beginn des Eingriffs:",
        "it": "Inizio della Procedura:",
        "nl": "Begin van de Ingreep:",
        "lb": "Ufank vum Agrëff:"
    },
    "Daqui a %.0f horas": {
        "pt": "Daqui a %.0f horas",
        "en": "In %.0f hours",
        "es": "En %.0f horas",
        "fr": "Dans %.0f heures",
        "de": "In %.0f Stunden",
        "it": "Tra %.0f ore",
        "nl": "Over %.0f uur",
        "lb": "An %.0f Stonnen"
    },
    "Iniciar Jejum & Ativar Alarmes": {
        "pt": "Iniciar Jejum & Ativar Alarmes",
        "en": "Start Fasting & Activate Alarms",
        "es": "Iniciar Ayuno y Activar Alarmas",
        "fr": "Démarrer le Jeûne & Activer les Alarmes",
        "de": "Fasten Starten & Alarme Aktivieren",
        "it": "Avvia Digiuno & Attiva Allarmi",
        "nl": "Start Vasten & Activeer Alarmen",
        "lb": "Faaschten Ufänken & Alarmen Aktivéieren"
    },
    "Protocolo de Segurança WSAVA / AAHA:": {
        "pt": "Protocolo de Segurança WSAVA / AAHA:",
        "en": "WSAVA / AAHA Safety Protocol:",
        "es": "Protocolo de Seguridad WSAVA / AAHA:",
        "fr": "Protocole de Sécurité WSAVA / AAHA :",
        "de": "WSAVA / AAHA Sicherheitsprotokoll:",
        "it": "Protocollo di Sicurezza WSAVA / AAHA:",
        "nl": "WSAVA / AAHA Veiligheidsprotocol:",
        "lb": "WSAVA / AAHA Sécherheetsprotokoll:"
    },
    "O cumprimento rigoroso dos tempos de jejum evita aspiração pulmonar durante a anestesia e garante a precisão de ecografias e análises bioquímicas.": {
        "pt": "O cumprimento rigoroso dos tempos de jejum evita aspiração pulmonar durante a anestesia e garante a precisão de ecografias e análises bioquímicas.",
        "en": "Strict adherence to fasting times prevents pulmonary aspiration during anesthesia and ensures accuracy of ultrasounds and blood tests.",
        "es": "El cumplimiento estricto del ayuno previene la aspiración pulmonar durante la anestesia y garantiza la precisión de ecografías y análisis.",
        "fr": "Le respect strict du jeûne évite l'aspiration pulmonaire sous anesthésie et garantit la précision des échographies et analyses.",
        "de": "Strenge Einhaltung der Fastenzeiten verhindert Lungenaspiration während Narkose und sichert Genauigkeit von Ultraschall und Bluttests.",
        "it": "Il rispetto rigoroso dei tempi di digiuno previene l'aspirazione polmonare durante l'anestesia e garantisce accuratezza delle analisi.",
        "nl": "Strikte naleving van vastentijden voorkomt longaspiratie tijdens anesthesie en waarborgt nauwkeurigheid van echo's en bloedonderzoek.",
        "lb": "Strikt Anhale vun de Faaschtenzäiten verhënnert Longenaspiratioun ënner Anästhesie a garantéiert Genauegkeet vun Analysen."
    },
    "Nenhum animal registado para iniciar o jejum.": {
        "pt": "Nenhum animal registado para iniciar o jejum.",
        "en": "No pet registered to start fasting.",
        "es": "Ninguna mascota registrada para iniciar el ayuno.",
        "fr": "Aucun animal enregistré pour démarrer le jeûne.",
        "de": "Kein Tier registriert, um das Fasten zu starten.",
        "it": "Nessun animale registrato per avviare il digiuno.",
        "nl": "Geen huisdier geregistreerd om het vasten te starten.",
        "lb": "Keng Déier registréiert fir d'Faaschten unzefänken."
    },
    "Notificações & Alarmes Clínicos": {
        "pt": "Notificações & Alarmes Clínicos",
        "en": "Notifications & Clinical Alarms",
        "es": "Notificaciones y Alarmas Clínicas",
        "fr": "Notifications & Alarmes Cliniques",
        "de": "Benachrichtigungen & Klinische Alarme",
        "it": "Notifiche & Allarmi Clinici",
        "nl": "Meldingen & Klinische Alarmen",
        "lb": "Notifikatiounen & Klinesch Alarmen"
    },
    "Alertas de Jejum Cirúrgico": {
        "pt": "Alertas de Jejum Cirúrgico",
        "en": "Surgical Fasting Alerts",
        "es": "Alertas de Ayuno Quirúrgico",
        "fr": "Alertes de Jeûne Chirurgical",
        "de": "Chirurgische Fastenalarme",
        "it": "Avvisi di Digiuno Chirurgico",
        "nl": "Chirurgische Vastenalarmen",
        "lb": "Chirurgesch Faaschtenalarmen"
    },
    "Avisos de corte de comida sólida e retirada de água": {
        "pt": "Avisos de corte de comida sólida e retirada de água",
        "en": "Warnings for solid food cut and water withdrawal",
        "es": "Avisos de corte de comida sólida y retirada de agua",
        "fr": "Avertissements d'arrêt d'aliments solides et de retrait d'eau",
        "de": "Hinweise zum Stopp von Festnahrung und Wasserentzug",
        "it": "Avvisi per l'interruzione di cibo solido e rimozione dell'acqua",
        "nl": "Waarschuwingen voor stop van vast voedsel en waterverwijdering",
        "lb": "Warnungen fir Festnahrungsstopp a Waasserentzug"
    },
    "Lembretes de Pesagem Periódica": {
        "pt": "Lembretes de Pesagem Periódica",
        "en": "Periodic Weigh-In Reminders",
        "es": "Recordatorios de Pesaje Periódico",
        "fr": "Rappels Périodiques de Pesée",
        "de": "Regelmäßige Wiege-Erinnerungen",
        "it": "Promemoria di Pesatura Periodica",
        "nl": "Periodieke Weegherinneringen",
        "lb": "Reegelméisseg Wiege-Erënnerungen"
    },
    "Acompanhamento quinzenal da curva de peso": {
        "pt": "Acompanhamento quinzenal da curva de peso",
        "en": "Bi-weekly monitoring of weight curve",
        "es": "Seguimiento quincenal de la curva de peso",
        "fr": "Suivi bimensuel de la courbe de poids",
        "de": "Zweiwöchentliche Überwachung der Gewichtskurve",
        "it": "Monitoraggio quindicinale della curva del peso",
        "nl": "Tweewekelijkse opvolging van gewichtscurve",
        "lb": "Zwou-Woche-Verfolgung vun der Gewiichtskurv"
    },
    "Notificação de teste enviada!": {
        "pt": "Notificação de teste enviada!",
        "en": "Test notification sent!",
        "es": "¡Notificación de prueba enviada!",
        "fr": "Notification de test envoyée !",
        "de": "Testbenachrichtigung gesendet!",
        "it": "Notifica di prova inviata!",
        "nl": "Testmelding verzonden!",
        "lb": "Testnotifikatioun geschéckt!"
    },
    "Testar Notificação de Sistema": {
        "pt": "Testar Notificação de Sistema",
        "en": "Test System Notification",
        "es": "Probar Notificación del Sistema",
        "fr": "Tester la Notification Système",
        "de": "Systembenachrichtigung Testen",
        "it": "Testa Notifica di Sistema",
        "nl": "Test Systeemmelding",
        "lb": "Systemnotifikatioun Testen"
    }
}

# 1. Update translations.json
with open("app/src/main/assets/translations.json", "r", encoding="utf-8") as f:
    translations = json.load(f)

for k, v in notification_translations.items():
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
    for k, trans in notification_translations.items():
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

print(f"Added {len(notification_translations)} new notification translation keys across all 8 languages successfully.")
