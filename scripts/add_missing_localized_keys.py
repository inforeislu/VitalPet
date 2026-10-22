#!/usr/bin/env python3
import json
import re

extra_missing_translations = {
    "Aplicada em: ": {
        "pt": "Aplicada em: ",
        "en": "Applied on: ",
        "es": "Aplicada el: ",
        "fr": "Appliquée le: ",
        "de": "Angewendet am: ",
        "it": "Somministrato il: ",
        "nl": "Toegepast op: ",
        "lb": "Ugewannt am: "
    },
    "Próx: ": {
        "pt": "Próx: ",
        "en": "Next: ",
        "es": "Próx: ",
        "fr": "Proch: ",
        "de": "Nächste: ",
        "it": "Pross: ",
        "nl": "Volgende: ",
        "lb": "Nächst: "
    },
    "Alergias / Cuidados: ": {
        "pt": "Alergias / Cuidados: ",
        "en": "Allergies / Care: ",
        "es": "Alergias / Cuidados: ",
        "fr": "Allergies / Soins: ",
        "de": "Allergien / Pflege: ",
        "it": "Allergie / Cure: ",
        "nl": "Allergieën / Zorg: ",
        "lb": "Allergien / Fleeg: "
    },
    "Sem fotos registadas nesta categoria.\nToque no botão abaixo para adicionar a primeira foto.": {
        "pt": "Sem fotos registadas nesta categoria.\nToque no botão abaixo para adicionar a primeira foto.",
        "en": "No photos registered in this category.\nTap the button below to add the first photo.",
        "es": "No hay fotos registradas en esta categoría.\nToque el botón de abajo para añadir la primera foto.",
        "fr": "Aucune photo enregistrée dans cette catégorie.\nAppuyez sur le bouton ci-dessous pour ajouter la première photo.",
        "de": "Keine Fotos in dieser Kategorie registriert.\nTippen Sie unten auf die Schaltfläche, um das erste Foto hinzuzufügen.",
        "it": "Nessuna foto registrata in questa categoria.\nTocca il pulsante in basso per aggiungere la prima foto.",
        "nl": "Geen foto's geregistreerd in deze categorie.\nTik op de knop hieronder om de eerste foto toe te voegen.",
        "lb": "Keng Fotoen an dëser Kategorie registréiert.\nTippen op de Knäppchen hei drënner fir déi éischt Foto derbäizesetzen."
    },
    "Nenhum sintoma digestivo ou clínico registado.\nToque no botão + para adicionar um novo registo.": {
        "pt": "Nenhum sintoma digestivo ou clínico registado.\nToque no botão + para adicionar um novo registo.",
        "en": "No digestive or clinical symptoms registered.\nTap the + button to add a new record.",
        "es": "No hay síntomas digestivos o clínicos registrados.\nToque el botón + para añadir un nuevo registro.",
        "fr": "Aucun symptôme digestif ou clinique enregistré.\nAppuyez sur le bouton + pour ajouter un nouvel enregistrement.",
        "de": "Keine Verdauungs- oder klinischen Symptome registriert.\nTippen Sie auf +, um einen neuen Eintrag hinzuzufügen.",
        "it": "Nessun sintomo digestivo o clinico registrato.\nTocca il pulsante + per aggiungere un nuovo registro.",
        "nl": "Geen spijsverterings- of klinische symptomen geregistreerd.\nTik op de + knop om un nieuw record toe te voegen.",
        "lb": "Keng Verdauungs- oder klinesch Symptomer registréiert.\nTippen op + fir en neien Eintrag derbäizesetzen."
    },
    "Nutrição & Culinária Veterinária": {
        "pt": "Nutrição & Culinária Veterinária",
        "en": "Veterinary Nutrition & Culinary",
        "es": "Nutrición y Culinaria Veterinaria",
        "fr": "Nutrition & Cuisine Vétérinaire",
        "de": "Veterinärernährung & Kochkunst",
        "it": "Nutrizione & Cucina Veterinaria",
        "nl": "Veterinaire Voeding & Culinair",
        "lb": "Veterinärernährung & Kachkonscht"
    },
    "Receitas seguras desenvolvidas para dietas terapêuticas e suporte digestivo.": {
        "pt": "Receitas seguras desenvolvidas para dietas terapêuticas e suporte digestivo.",
        "en": "Safe recipes developed for therapeutic diets and digestive support.",
        "es": "Recetas seguras desarrolladas para dietas terapéuticas y soporte digestivo.",
        "fr": "Recettes sûres développées pour les régimes thérapeutiques et le soutien digestif.",
        "de": "Sichere Rezepte entwickelt für therapeutische Diäten und Verdauungsunterstützung.",
        "it": "Ricette sicure sviluppate per diete terapeutiche e supporto digestivo.",
        "nl": "Veilige recepten ontwikkeld voor therapeutische diëten en spijsverteringsondersteuning.",
        "lb": "Sicher Rezepter entwéckelt fir therapeutesch Diäten a Verdauungsënnerstëtzung."
    },
    "Dieta Branda Gastrointestinal": {
        "pt": "Dieta Branda Gastrointestinal",
        "en": "Gastrointestinal Bland Diet",
        "es": "Dieta Blanda Gastrointestinal",
        "fr": "Régime Doux Gastro-intestinal",
        "de": "Schonkost für Magen und Darm",
        "it": "Dieta Leggera Gastrointestinale",
        "nl": "Licht Verteerbaar Maag-darmdieet",
        "lb": "Magen-Darm Schounkost"
    },
    "Recuperação Gástrica": {
        "pt": "Recuperação Gástrica",
        "en": "Gastric Recovery",
        "es": "Recuperación Gástrica",
        "fr": "Récupération Gastrique",
        "de": "Magen-Erholung",
        "it": "Recupero Gastrico",
        "nl": "Maagherstel",
        "lb": "Magen-Erholung"
    },
    "Indicada para episódios de vómitos transitórios, diarreia leve ou pós-cirúrgico gastrointestinal.": {
        "pt": "Indicada para episódios de vómitos transitórios, diarreia leve ou pós-cirúrgico gastrointestinal.",
        "en": "Indicated for transient vomiting episodes, mild diarrhea, or post-gastrointestinal surgery.",
        "es": "Indicada para episodios de vómitos transitorios, diarrea leve o posquirúrgico gastrointestinal.",
        "fr": "Indiquée pour les épisodes de vomissements passagers, la diarrhée légère ou la récupération post-chirurgicale.",
        "de": "Indiziert bei vorübergehendem Erbrechen, leichtem Durchfall oder nach Magen-Darm-Operationen.",
        "it": "Indicata per episodi di vomito transitorio, diarrea lieve o post-chirurgico gastrointestinale.",
        "nl": "Aangegeven bij tijdelijk braken, milde diarree of herstel na een maag-darmoperatie.",
        "lb": "Endizéiert bei viriwwergehendem Erbriechen, liichtem Diarrho oder no Magen-Darm-Operatiounen."
    },
    "60% Peito de frango cozido sem pele, sem ossos e sem sal": {
        "pt": "60% Peito de frango cozido sem pele, sem ossos e sem sal",
        "en": "60% Cooked skinless, boneless, un-salted chicken breast",
        "es": "60% Pechuga de pollo cocida sin piel, sin huesos y sin sal",
        "fr": "60% Poitrine de poulet cuite sans peau, sans os et sans sel",
        "de": "60% Gekochte Hähnchenbrust ohne Haut, Knochen und Salz",
        "it": "60% Petto di pollo cotto senza pelle, senza ossa e senza sale",
        "nl": "60% Gekookte kipfilet zonder vel, bot en zout",
        "lb": "60% Gekacht Hähnchenbrust ouni Haut, Knochen a Salz"
    },
    "40% Arroz branco cozido bem empapado (amido digestível)": {
        "pt": "40% Arroz branco cozido bem empapado (amido digestível)",
        "en": "40% Well-cooked over-soft white rice (digestible starch)",
        "es": "40% Arroz blanco cocido bien blando (almidón digestible)",
        "fr": "40% Riz blanc bien cuit (amidon digestible)",
        "de": "40% Gut weich gekochter weißer Reis (leicht verdauliche Stärke)",
        "it": "40% Riso bianco molto cotto e morbido (amido digeribile)",
        "nl": "40% Goed doorgekookte witte rijst (verteerbaar zetmeel)",
        "lb": "40% Gutt mëll gekachten wäisse Räis (verdauliche Stärke)"
    },
    "Opcional: 1 colher de puré de abóbora cozida (fonte de fibra solúvel)": {
        "pt": "Opcional: 1 colher de puré de abóbora cozida (fonte de fibra solúvel)",
        "en": "Optional: 1 tbsp cooked pumpkin puree (soluble fiber source)",
        "es": "Opcional: 1 cucharada de puré de calabaza cocida (fuente de fibra soluble)",
        "fr": "Optionnel: 1 cuillère de purée de courge cuite (source de fibres solubles)",
        "de": "Optional: 1 EL gekochtes Kürpispüree (lösliche Ballaststoffquelle)",
        "it": "Opzionale: 1 cucchiaio di purea di zucca cotta (fonte di fibra solubile)",
        "nl": "Optioneel: 1 el gekookte pompoenpuree (bron van oplosbare vezels)",
        "lb": "Optional: 1 Iessläffel gekachte Kürbispüree (léislech Ballaststoffquell)"
    },
    "Coza o peito de frango e desfie finamente. Coza o arroz em abundância de água até ficar macio. Misture sem adicionar sal, alho, cebola ou gordura. Sirva morno em pequenas porções ao longo do dia.": {
        "pt": "Coza o peito de frango e desfie finamente. Coza o arroz em abundância de água até ficar macio. Misture sem adicionar sal, alho, cebola ou gordura. Sirva morno em pequenas porções ao longo do dia.",
        "en": "Boil chicken breast and shred finely. Cook rice in plenty of water until soft. Mix without adding salt, garlic, onion, or fat. Serve warm in small portions throughout the day.",
        "es": "Coce la pechuga de pollo y desmenúzela finamente. Coce el arroz con abundante agua hasta que quede blando. Mezcle sin añadir sal, ajo, cebolla ni grasa. Sirva tibio en pequeñas porciones.",
        "fr": "Faites cuire le poulet et effilochez-le finement. Cuisez le riz dans beaucoup d'eau jusqu'à ce qu'il soit très tendre. Mélangez sans sel, ail, oignon ni matière grasse. Servez tiède.",
        "de": "Kochen Sie die Hähnchenbrust und zerkleinern Sie sie fein. Kochen Sie den Reis in reichlich Wasser weich. Mischen ohne Salz, Knoblauch, Zwiebeln oder Fett. Lauwarm in kleinen Portionen servieren.",
        "it": "Lessa il petto di pollo e sfilaccialo finemente. Cuoci il riso in abbondante acqua finché è morbido. Mescola senza sale, aglio, cipolla o grassi. Servi tiepido a piccole porzioni.",
        "nl": "Kook de kipfilet en versnipper fijn. Kook de rijst in ruim water zacht. Meng zonder zout, knoflook, ui of vet. Lauwwarm in kleine porties serveren.",
        "lb": "Kacht d'Hähnchenbrust an zerkleinert se fein. Kacht de Räis a vill Wasser mëll. Mëscht ouni Salz, Knuewelek, Zwiwwel oder Fett. Lauwarm a klenge Portiounen servéieren."
    },
    "Caldo de Ossos Suave (Articular & Renal)": {
        "pt": "Caldo de Ossos Suave (Articular & Renal)",
        "en": "Gentle Bone Broth (Joint & Renal)",
        "es": "Caldo de Huesos Suave (Articular y Renal)",
        "fr": "Bouillon d'Os Doux (Articulaire & Rénal)",
        "de": "Milder Knochenbrühe (Gelenke & Nieren)",
        "it": "Brodo di Ossa Delicato (Articolare & Renale)",
        "nl": "Zachte Bottenbouillon (Gewrichten & Nieren)",
        "lb": "Mëll Knochenbrühe (Gelenker & Nieren)"
    },
    "Hidratação & Suporte Articular": {
        "pt": "Hidratação & Suporte Articular",
        "en": "Hydration & Joint Support",
        "es": "Hidratación y Soporte Articular",
        "fr": "Hydratation & Soutien Articulaire",
        "de": "Hydratation & Gelenkunterstützung",
        "it": "Idratazione & Supporto Articolare",
        "nl": "Hydratatie & Gewrichtsondersteuning",
        "lb": "Hydratatioun & Gelenkënnerstëtzung"
    },
    "Rico em colagénio, glicosamina e minerais essenciais. Excelente para hidratação e estímulo de apetite.": {
        "pt": "Rico em colagénio, glicosamina e minerais essenciais. Excelente para hidratação e estímulo de apetite.",
        "en": "Rich in collagen, glucosamine, and essential minerals. Excellent for hydration and appetite stimulation.",
        "es": "Rico en colágeno, glucosamina y minerales esenciales. Excelente para hidratación y estimulación del apetito.",
        "fr": "Riche en collagène, glucosamine et minéraux essentiels. Excellent pour l'hydratation et stimuler l'appétit.",
        "de": "Reich an Kollagen, Glucosamin und essentiellen Mineralien. Hervorragend zur Hydratisierung und Appetitanregung.",
        "it": "Ricco di collagene, glucosamina e minerali essenziali. Eccellente per l'idratazione e la stimolazione dell'appetito.",
        "nl": "Rijk aan collageen, glucosamine en essentiële mineralen. Uitstekend voor hydratatie en eetluststimulatie.",
        "lb": "Räich un Kollagen, Glukosamin an essentiellen Mineralien. Exzellent fir Hydratatioun an Appetitstimulatioun."
    },
    "1 kg de ossos carnudos de vaca ou frango": {
        "pt": "1 kg de ossos carnudos de vaca ou frango",
        "en": "1 kg raw beef or chicken meaty bones",
        "es": "1 kg de huesos carnosos de vaca o pollo",
        "fr": "1 kg d'os charnus de bœuf ou de poulet",
        "de": "1 kg fleischige Rinder- oder Hühnerknochen",
        "it": "1 kg di ossa carnose di manzo o pollo",
        "nl": "1 kg vlezige runder- of kippenbotten",
        "lb": "1 kg fleescheg Rënder- oder Hähnchenknochen"
    },
    "2 litros de água filtrada": {
        "pt": "2 litros de água filtrada",
        "en": "2 liters filtered water",
        "es": "2 litros de agua filtrada",
        "fr": "2 litres d'eau filtrée",
        "de": "2 Liter gefiltertes Wasser",
        "it": "2 litri di acqua filtrata",
        "nl": "2 liter gefilterd water",
        "lb": "2 Liter gefiltert Wasser"
    },
    "1 colher de sopa de vinagre de sidra (para extrair minerais)": {
        "pt": "1 colher de sopa de vinagre de sidra (para extrair minerais)",
        "en": "1 tbsp apple cider vinegar (to extract minerals)",
        "es": "1 cucharada de vinagre de sidra de manzana (para extraer minerales)",
        "fr": "1 cuillère à soupe de vinaigre de cidre (pour extraire les minéraux)",
        "de": "1 EL Apfelessig (zur Mineralienextraktion)",
        "it": "1 cucchiaio di aceto di mele (per estrarre i minerali)",
        "nl": "1 el appelciderazijn (om mineralen te extractor)",
        "lb": "1 Iessläffel Apfelesseg (fir Mineralien ze extrahéieren)"
    },
    "Coloque os ossos, água e vinagre numa panela. Cozinhe em lume muito brando durante 6 a 12 horas. Coe com um passador fino descartando TODOS os ossos. Deixe arrefecer no frigorífico e remova a camada de gordura superficial antes de servir.": {
        "pt": "Coloque os ossos, água e vinagre numa panela. Cozinhe em lume muito brando durante 6 a 12 horas. Coe com um passador fino descartando TODOS os ossos. Deixe arrefecer no frigorífico e remova a camada de gordura superficial antes de servir.",
        "en": "Place bones, water, and vinegar in a pot. Simmer on low heat for 6 to 12 hours. Strain through a fine mesh sieve discarding ALL bones. Cool in fridge and skim off top fat layer before serving.",
        "es": "Coloque los huesos, agua y vinagre en una olla. Cocine a fuego muy lento de 6 a 12 horas. Cuele descartando TODOS los huesos. Deje enfriar en la nevera y retire la capa de grasa superior.",
        "fr": "Mettez les os, l'eau et le vinaigre dans une casserole. Laissez mijoter à feu doux de 6 à 12 heures. Filtrez en jetant TOUS les os. Laissez refroidir et retirez la graisse en surface.",
        "de": "Knochen, Wasser und Essig in einen Topf geben. bei niedriger Hitze 6-12 Stunden köcheln lassen. Durch ein feines Sieb seihen und ALLE Knochen wegwerfen. Im Kühlschrank abkühlen lassen und Fettchicht entfernen.",
        "it": "Metti ossa, acqua e aceto in una pentola. Cuoci a fuoco lentissimo per 6-12 ore. Filtra eliminando TUTTE le ossa. Lascia raffreddare in frigo e rimuovi lo strato di grasso superficiale.",
        "nl": "Doe botten, water en azijn in een pan. Laat 6 tot 12 uur op laag vuur sudderen. Zeef zorgvuldig en gooi ALLE botten weg. Laat afkoelen in de koelkast en verwijder de vetlaag.",
        "lb": "Knochen, Wasser an Esseg an en Dëppen ginn. Bei niddreger Hëtzt 6 bis 12 Stonnen köchelen loossen. Duerch en feint Siww seihen an ALL Knochen ewechwerfen. Am Frigo ofkillen loossen an de Fettfilm ewechhuelen."
    },
    "Refeição Cozida Completa (Carne & Legumes)": {
        "pt": "Refeição Cozida Completa (Carne & Legumes)",
        "en": "Complete Cooked Meal (Meat & Veggies)",
        "es": "Comida Cocida Completa (Carne y Verduras)",
        "fr": "Repas Cuit Complet (Viande & Légumes)",
        "de": "Vollständiges gekochtes Menü (Fleisch & Gemüse)",
        "it": "Pasto Cotto Completo (Carne & Verdure)",
        "nl": "Volledige Gekookte Maaltijd (Vlees & Groenten)",
        "lb": "Vollstänneg gekacht Moolzecht (Fleesch & Geméis)"
    },
    "Alimentação Natural Cozida": {
        "pt": "Alimentação Natural Cozida",
        "en": "Cooked Natural Diet",
        "es": "Alimentación Natural Cocida",
        "fr": "Alimentation Naturelle Cuite",
        "de": "Gekochte Naturnahrung",
        "it": "Alimentazione Naturale Cotta",
        "nl": "Gekookte Natuurlijke Voeding",
        "lb": "Gekacht Natuernahrung"
    },
    "Alternativa nutritiva a ração industrial sob orientação nutrológica veterinária.": {
        "pt": "Alternativa nutritiva a ração industrial sob orientação nutrológica veterinária.",
        "en": "Nutritious alternative to commercial kibble under veterinary guidance.",
        "es": "Alternativa nutritiva al pienso comercial bajo orientación veterinaria.",
        "fr": "Alternative nutritive aux croquettes industrielles sous conseil vétérinaire.",
        "de": "Nahrhafte Alternative zu kommerziellem Trockenfutter unter tierärztlicher Anleitung.",
        "it": "Nutriente alternativa al cibo industriale sotto guida nutrizionale veterinaria.",
        "nl": "Voedzaam alternatief voor commerciële brokjes onder veterinair advies.",
        "lb": "Nahrhaft Alternativ zu kommerziellem Trockenfudder ënner tierärztlecher Leedung."
    },
    "50% Carne magra (peru, frango ou vaca magra cortada em cubos)": {
        "pt": "50% Carne magra (peru, frango ou vaca magra cortada em cubos)",
        "en": "50% Lean meat (diced turkey, chicken, or lean beef)",
        "es": "50% Carne magra (pavo, pollo o ternera magra en cubos)",
        "fr": "50% Viande maigre (dinde, poulet ou bœuf maigre en dés)",
        "de": "50% Mageres Fleisch (Puten-, Hähnchen- oder mageres Rindfleisch gewürfelt)",
        "it": "50% Carne magra (tacchino, pollo o manzo magro a cubetti)",
        "nl": "50% Mager vlees (kalkoen, kip of mager rundvlees in blokjes)",
        "lb": "50% Mageres Fleesch (Puten-, Hähnchen- oder mageres Rënderfleesch gewürfelt)"
    },
    "20% Fígado e coração bovino cozidos": {
        "pt": "20% Fígado e coração bovino cozidos",
        "en": "20% Cooked beef liver and heart",
        "es": "20% Hígado y corazón de vacuno cocidos",
        "fr": "20% Foie et cœur de bœuf cuits",
        "de": "20% Gekochte Rinderleber und Rinderherz",
        "it": "20% Fegato e cuore di manzo cotti",
        "nl": "20% Gekookte runderlever en runderhart",
        "lb": "20% Gekacht Rënderleber an Häerz"
    },
    "20% Vegetais seguros (cenoura, abobrinha, feijão-verde cozidos)": {
        "pt": "20% Vegetais seguros (cenoura, abobrinha, feijão-verde cozidos)",
        "en": "20% Safe cooked vegetables (carrots, zucchini, green beans)",
        "es": "20% Verduras seguras cocidas (zanahoria, calabacín, judías verdes)",
        "fr": "20% Légumes cuits sûrs (carottes, courgettes, haricots verts)",
        "de": "20% Sicheres gekochtes Gemüse (Karotten, Zucchini, grüne Bohnen)",
        "it": "20% Verdure cotte sicure (carote, zucchine, fagiolini)",
        "nl": "20% Veilige gekookte groenten (wortels, courgette, sperziebonen)",
        "lb": "20% Sicher gekacht Geméis (Muerten, Zucchini, gréng Bounen)"
    },
    "10% Batata-doce ou aveia integral cozida": {
        "pt": "10% Batata-doce ou aveia integral cozida",
        "en": "10% Cooked sweet potato or whole oats",
        "es": "10% Camote o avena integral cocida",
        "fr": "10% Patate douce cuite ou flocons d'avoine",
        "de": "10% Gekochte Süßkartoffel oder Vollkornhafer",
        "it": "10% Patata dolce cotta o avena integrale",
        "nl": "10% Gekookte zoete aardappel of volkoren haver",
        "lb": "10% Gekacht Séisskartoffel oder Vollkornhafer"
    },
    "Cozinhe a carne e os vegetais a vapor ou com pouca água para preservar nutrientes. Pique e misture tudo. Adicione um suplemento vitamínico-mineral prescrito pelo médico veterinário.": {
        "pt": "Cozinhe a carne e os vegetais a vapor ou com pouca água para preservar nutrientes. Pique e misture tudo. Adicione um suplemento vitamínico-mineral prescrito pelo médico veterinário.",
        "en": "Steam or cook meat and veggies in minimal water to preserve nutrients. Chop and mix well. Add a vitamin-mineral supplement prescribed by your veterinarian.",
        "es": "Cocine la carne y verduras al vapor o con poca agua para preservar nutrientes. Pique y mezcle todo. Añada un suplemento vitamínico-mineral prescrito por el veterinario.",
        "fr": "Cuisez la viande et les légumes à la vapeur pour préserver les nutriments. Hachez et mélangez. Ajoutez un supplément de vitamines prescrit par votre vétérinaire.",
        "de": "Dämpfen Sie Fleisch und Gemüse schonend. Fein hacken und mischen. Fügen Sie ein vom Tierarzt verschriebenes Vitamin-Mineral-Präparat hinzu.",
        "it": "Cuoci la carne e le verdure al vapore o con poca acqua. Trita e mescola. Aggiungi un integratore di vitamine e minerali prescritto dal veterinario.",
        "nl": "Stoom vlees en groenten om voedingsstoffen te behouden. Hak fijn en meng. Voeg un door de dierenarts voorgeschreven vitamine-mineraalsupplement toe.",
        "lb": "Dämpft Fleesch a Geméis schonend. Fein hacken an mëschen. Fügt en vom Déierenarzt verschriwwene Vitamin-Mineral-Zusaz derbäi."
    },
    "Snacks Naturais de Cenoura & Aveia": {
        "pt": "Snacks Naturais de Cenoura & Aveia",
        "en": "Natural Carrot & Oat Treats",
        "es": "Snacks Naturales de Zanahoria y Avena",
        "fr": "Friandises Naturelles Carotte & Avoine",
        "de": "Natürliche Karotten- & Hafer-Snacks",
        "it": "Snack Naturali Carota & Avena",
        "nl": "Natuurlijke Wortel- & Haversnacks",
        "lb": "Natierlech Muerten- & Hafer-Snacks"
    },
    "Petiscos Saudáveis": {
        "pt": "Petiscos Saudáveis",
        "en": "Healthy Treats",
        "es": "Premios Saludables",
        "fr": "Friandises Saines",
        "de": "Gesunde Leckerlis",
        "it": "Snack Sani",
        "nl": "Gezonde Tussendoortjes",
        "lb": "Gesond Leckerlien"
    },
    "Petisco crocante de baixo teor calórico para treino e recompensas saudáveis.": {
        "pt": "Petisco crocante de baixo teor calórico para treino e recompensas saudáveis.",
        "en": "Low-calorie crunchy treat for training and healthy rewards.",
        "es": "Premio crujiente de bajo contenido calórico para entrenamiento y recompensas.",
        "fr": "Friandise croustillante à faible calorie pour l'entraînement.",
        "de": "Knuspriger kalorienarmer Snack für Training und Belohnung.",
        "it": "Snack croccante a basso contenuto calorico per addestramento e ricompense.",
        "nl": "Knapperige caloriearme snack voor training en gezonde beloningen.",
        "lb": "Knusprege kalorieschwaache Snack fir Training an Belounung."
    },
    "1 chávena de farinha de aveia integral": {
        "pt": "1 chávena de farinha de aveia integral",
        "en": "1 cup whole oat flour",
        "es": "1 taza de harina de avena integral",
        "fr": "1 tasse de farine d'avoine complète",
        "de": "1 Tasse Vollkornhafermehl",
        "it": "1 tazza di farina di avena integrale",
        "nl": "1 kop volkoren havermeel",
        "lb": "1 Tass Vollkornhafermehl"
    },
    "1 cenoura média ralada finamente": {
        "pt": "1 cenoura média ralada finamente",
        "en": "1 medium carrot finely grated",
        "es": "1 zanahoria mediana rallada finamente",
        "fr": "1 carotte moyenne râpée finement",
        "de": "1 mittlere Karotte fein geraspelt",
        "it": "1 carota media finemente grattugiata",
        "nl": "1 middelgrote fijngeraspte wortel",
        "lb": "1 mëttel Muert fein geraspelt"
    },
    "1 ovo inteiro batido": {
        "pt": "1 ovo inteiro batido",
        "en": "1 whole egg beaten",
        "es": "1 huevo entero batido",
        "fr": "1 œuf entier battu",
        "de": "1 ganzes Ei verquirlt",
        "it": "1 uovo intero sbattuto",
        "nl": "1 heel geklutst ei",
        "lb": "1 ganzes Ee verquirlt"
    },
    "2 colheres de água se necessário": {
        "pt": "2 colheres de água se necessário",
        "en": "2 tbsp water if needed",
        "es": "2 cucharadas de agua si es necesario",
        "fr": "2 cuillères d'eau si nécessaire",
        "de": "2 EL Wasser falls nötig",
        "it": "2 cucchiai di acqua se necessario",
        "nl": "2 el water indien nodig",
        "lb": "2 Iessläffelen Wasser falls néideg"
    },
    "Misture os ingredientes até obter uma massa moldável. Estenda e corte em pequenos formatos. Asse a 180°C durante 15 a 20 minutos até ficarem dourados e crocantes. Conserve em frasco hermético.": {
        "pt": "Misture os ingredientes até obter uma massa moldável. Estenda e corte em pequenos formatos. Asse a 180°C durante 15 a 20 minutos até ficarem dourados e crocantes. Conserve em frasco hermético.",
        "en": "Mix ingredients to form a dough. Roll out and cut into small shapes. Bake at 180°C (350°F) for 15-20 min until golden and crunchy. Store in an airtight container.",
        "es": "Mezcle los ingredientes hasta formar una masa. Extienda y corte en formas pequeñas. Hornee a 180°C durante 15-20 minutos hasta que estén dorados y crujientes. Guardar en frasco hermético.",
        "fr": "Mélangez les ingrédients jusqu'à former une pâte. Étale et découpez en petites formes. Cuire à 180°C pendant 15-20 min jusqu'à doré. Conserver dans un bocal hermétique.",
        "de": "Zutaten zu einem Teig vermengen. Ausrollen und in kleine Formen schneiden. Bei 180°C 15-20 Minuten backen, bis sie goldbraun und knusprig sind. Luftdicht aufbewahren.",
        "it": "Mescola gli ingredienti fino a formare un impasto. Stendi e taglia in piccole forme. Cuoci a 180°C per 15-20 minuti finché dorati. Conserva in un barattolo ermetico.",
        "nl": "Meng ingrediënten tot un deeg. Rol uit en snijd in kleine vormpjes. Bak 15-20 minuten op 180°C tot ze goudbruin en knapperig zijn. Bewaar in luchtdichte pot.",
        "lb": "Zutaten zu engem Deeg vermengen. Ausrollen an a kleng Formen schneiden. Bei 180°C 15-20 Minutte baken, bis se goldbraun an knuspreg sinn. Luftdicht opbewahren."
    },
    "Indicações:": {
        "pt": "Indicações:",
        "en": "Indications:",
        "es": "Indicaciones:",
        "fr": "Indications:",
        "de": "Indikationen:",
        "it": "Indicazioni:",
        "nl": "Indicaties:",
        "lb": "Indikatiounen:"
    },
    "Ingredientes:": {
        "pt": "Ingredientes:",
        "en": "Ingredients:",
        "es": "Ingredientes:",
        "fr": "Ingrédients:",
        "de": "Zutaten:",
        "it": "Ingredienti:",
        "nl": "Ingrediënten:",
        "lb": "Zutaten:"
    },
    "Modo de Preparo:": {
        "pt": "Modo de Preparo:",
        "en": "Preparation:",
        "es": "Modo de Preparación:",
        "fr": "Préparation:",
        "de": "Zubereitung:",
        "it": "Preparazione:",
        "nl": "Bereidingswijze:",
        "lb": "Zubereitung:"
    },
    "Idioma da Aplicação (8 Idiomas Oficiais)": {
        "pt": "Idioma da Aplicação (8 Idiomas Oficiais)",
        "en": "App Language (8 Official Languages)",
        "es": "Idioma de la Aplicación (8 Idiomas Oficiales)",
        "fr": "Langue de l'Application (8 Langues Officielles)",
        "de": "App-Sprache (8 offizielle Sprachen)",
        "it": "Lingua dell'App (8 Lingue Ufficiali)",
        "nl": "App-taal (8 officiële talen)",
        "lb": "App Sprooch (8 offiziell Sproochen)"
    }
}

with open("app/src/main/assets/translations.json", "r", encoding="utf-8") as f:
    translations = json.load(f)

for k, v in extra_missing_translations.items():
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
    for k, trans in extra_missing_translations.items():
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

print("Successfully added all remaining missing translation keys.")
