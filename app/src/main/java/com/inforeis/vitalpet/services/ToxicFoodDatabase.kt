package com.inforeis.vitalpet.services

import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.models.ToxicCategory
import com.inforeis.vitalpet.models.ToxicFood
import com.inforeis.vitalpet.models.ToxicityLevel

object ToxicFoodDatabase {
    val toxicItems: List<ToxicFood> = listOf(
        ToxicFood(
            name = "Chocolate e Cacau",
            toxicAgent = "Teobromina e Cafeína",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.FOODS,
            affectedSpecies = null,
            symptoms = "Vómitos, diarreia, tremores, arritmia cardíaca, taquicardia severa e convulsões.",
            firstAidAdvice = "Contacto urgente com clínica 24h. Guardar embalagem para calcular dose tóxica."
        ),
        ToxicFood(
            name = "Uvas e Passas",
            toxicAgent = "Ácido Tartárico",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.FOODS,
            affectedSpecies = Species.DOG,
            symptoms = "Insuficiência renal aguda, vómitos repetidos, letargia profunda e anúria.",
            firstAidAdvice = "Emergência canina imediata! Mesmo 2 ou 3 passas podem induzir falência renal em cães."
        ),
        ToxicFood(
            name = "Cebola, Alho, Alho-Francês e Cebolinho",
            toxicAgent = "N-propil dissulfeto",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.FOODS,
            affectedSpecies = null,
            symptoms = "Destruição de glóbulos vermelhos (anemia hemolítica), corpos de Heinz, urina castanha avermelhada, gengivas pálidas. Felinos são 3 vezes mais sensíveis que cães.",
            firstAidAdvice = "Levar à clínica para exames sanguíneos e controlo de hematócrito. Não induzir o vómito."
        ),
        ToxicFood(
            name = "Xilitol (Adoçante de Pastilhas e Doces)",
            toxicAgent = "Xilitol",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.FOODS,
            affectedSpecies = Species.DOG,
            symptoms = "Libertação massiva de insulina provocando hipoglicemia fulminante, tremores, ataxia e necrose hepática aguda.",
            firstAidAdvice = "Emergência máxima! Oferecer um pouco de mel ou xarope nas gengivas e correr para o veterinário."
        ),
        ToxicFood(
            name = "Massa Crua com Fermento",
            toxicAgent = "Etanol e fermentação gasosa",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.FOODS,
            affectedSpecies = Species.DOG,
            symptoms = "Distensão gástrica dolorosa, torção de estômago e intoxicação alcoólica severa.",
            firstAidAdvice = "Urgência imediata. O fermento continua a expandir no estômago morno do animal."
        ),
        ToxicFood(
            name = "Bebidas Alcoólicas e Etanol",
            toxicAgent = "Etanol",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.FOODS,
            affectedSpecies = null,
            symptoms = "Acidose metabólica, hipotermia, depressão respiratória e paragem cardíaca.",
            firstAidAdvice = "Contacto veterinário urgente. Manter o animal aquecido durante o transporte."
        ),
        ToxicFood(
            name = "Café, Chá Preto e Bebidas Energéticas",
            toxicAgent = "Metilxantinas e Cafeína",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.FOODS,
            affectedSpecies = null,
            symptoms = "Hiperatividade, hipertensão, taquicardia ventricular e tremores musculares.",
            firstAidAdvice = "Manter em ambiente escuro e calmo e procurar atendimento médico imediato."
        ),
        ToxicFood(
            name = "Nozes de Macadâmia",
            toxicAgent = "Toxina desconhecida",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.FOODS,
            affectedSpecies = Species.DOG,
            symptoms = "Fraqueza e paralisia dos membros posteriores, tremores, rigidez e hipertermia.",
            firstAidAdvice = "Repouso e suporte veterinário para hidratação e controlo de febre."
        ),
        ToxicFood(
            name = "Abacate",
            toxicAgent = "Persina",
            level = ToxicityLevel.MODERATE,
            category = ToxicCategory.FOODS,
            affectedSpecies = null,
            symptoms = "Vómitos, diarreia e dificuldade respiratória por retenção hídrica em aves e pequenos carnívoros.",
            firstAidAdvice = "Retirar de circulação e oferecer água fresca. O caroço também apresenta risco de obstrução fatal."
        ),
        ToxicFood(
            name = "Caroços de Frutos (Pêssego, Cereja, Ameixa, Maçã)",
            toxicAgent = "Glicosídeos cianogénicos (Cianeto)",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.FOODS,
            affectedSpecies = null,
            symptoms = "Midríase (pupilas dilatadas), salivação, gengivas vermelho-vivo, choque e asfixia celular.",
            firstAidAdvice = "Procurar socorro veterinário urgente se o caroço tiver sido mastigado."
        ),
        ToxicFood(
            name = "Ossos Cozidos (Frango, Costeletas)",
            toxicAgent = "Fragmentação mecânica cortante",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.FOODS,
            affectedSpecies = null,
            symptoms = "Perfuração gástrica ou intestinal, obstrução esofágica, peritonite séptica e hemorragia retal.",
            firstAidAdvice = "NÃO induzir o vómito para não perfurar o esófago. Fazer radiografia na clínica veterinária."
        ),
        ToxicFood(
            name = "Sal em Excesso e Salgadinhos",
            toxicAgent = "Iões de Sódio (Hipernatremia)",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.FOODS,
            affectedSpecies = null,
            symptoms = "Edema cerebral, sede insaciável, tremores, diarreia e convulsões epiléticas.",
            firstAidAdvice = "Oferecer pequenas quantidades de água e levar para fluidoterapia controlada no hospital."
        ),
        ToxicFood(
            name = "Lúpulo de Cerveja (Fresco ou Seco)",
            toxicAgent = "Compostos amargos de lúpulo",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.FOODS,
            affectedSpecies = Species.DOG,
            symptoms = "Hipertermia maligna canina (temperatura > 41°C), taquicardia extrema e coagulação intravascular.",
            firstAidAdvice = "Emergência canina com arrefecimento ativo e tratamento intensivo imediato."
        ),
        ToxicFood(
            name = "Cogumelos Selvagens Não Identificados",
            toxicAgent = "Amanitinas, Muscarina e Psilocibina",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.FOODS,
            affectedSpecies = null,
            symptoms = "Insuficiência hepática fulminante, vómitos hemorrágicos, delírio e coma.",
            firstAidAdvice = "Recolher uma amostra do cogumelo num saco e correr para o hospital veterinário."
        ),
        ToxicFood(
            name = "Alimentos Bolorentos e Estragados",
            toxicAgent = "Micotoxinas tremorgénicas (Roquefortina)",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.FOODS,
            affectedSpecies = null,
            symptoms = "Tremores musculares contínuos, febre, convulsões e rigidez postural.",
            firstAidAdvice = "Hospitalização imediata com lavagem gástrica e administração de carvão ativado."
        ),
        ToxicFood(
            name = "Lírios e Liliáceas (Lilium e Hemerocallis)",
            toxicAgent = "Toxina liliácea desconhecida",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.PLANTS,
            affectedSpecies = Species.CAT,
            symptoms = "Insuficiência renal aguda fulminante e fatal em gatos. Mesmo o pólen no pelo ou lamber a água do vaso é letal.",
            firstAidAdvice = "Emergência felina máxima! Lavar o pelo do gato para evitar que se lamba e iniciar fluidoterapia venosa nas primeiras 6 horas."
        ),
        ToxicFood(
            name = "Estrela-de-Natal (Poinsétia)",
            toxicAgent = "Ésteres diterpénicos",
            level = ToxicityLevel.MODERATE,
            category = ToxicCategory.PLANTS,
            affectedSpecies = null,
            symptoms = "Irritação severa da mucosa oral, salivação excessiva, gastrite com vómitos e conjuntivite.",
            firstAidAdvice = "Lavar a boca do pet com água corrente fresca para diluir a seiva leitosa e monitorizar."
        ),
        ToxicFood(
            name = "Costela-de-Adão (Monstera Deliciosa)",
            toxicAgent = "Cristais insolúveis de Oxalato de Cálcio",
            level = ToxicityLevel.MODERATE,
            category = ToxicCategory.PLANTS,
            affectedSpecies = null,
            symptoms = "Sensação de queimadura na boca, edema de língua, sialorreia intensa e disfagia (dor ao engolir).",
            firstAidAdvice = "Oferecer um pouco de iogurte natural ou leite frio para precipitar os cristais e procurar veterinário."
        ),
        ToxicFood(
            name = "Espada-de-São-Jorge (Sansevieria)",
            toxicAgent = "Saponinas e glicosídeos esteroides",
            level = ToxicityLevel.MODERATE,
            category = ToxicCategory.PLANTS,
            affectedSpecies = null,
            symptoms = "Diarreia aquosa, salivação, náuseas e apatia em cães e gatos.",
            firstAidAdvice = "Remover resíduos da boca e manter o animal hidratado. Consultar clínica se persistir."
        ),
        ToxicFood(
            name = "Comigo-Ninguém-Pode (Dieffenbachia)",
            toxicAgent = "Oxalatos insolúveis e enzimas proteolíticas",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.PLANTS,
            affectedSpecies = null,
            symptoms = "Edema agudo de glote com risco de asfixia, queimaduras orais severas e cegueira se atingir os olhos.",
            firstAidAdvice = "Emergência veterinária. Aplicar compressas frescas e verificar desobstrução das vias aéreas."
        ),
        ToxicFood(
            name = "Azáleas e Rododendros",
            toxicAgent = "Grayanotoxina",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.PLANTS,
            affectedSpecies = null,
            symptoms = "Bloqueio atrioventricular, hipotensão severa, vómitos contínuos e fraqueza muscular.",
            firstAidAdvice = "Hospitalização urgente com suporte eletrocardiográfico e reposição de fluidos."
        ),
        ToxicFood(
            name = "Cannabis / Canábis (THC)",
            toxicAgent = "Tetrahidrocanabinol",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.PLANTS,
            affectedSpecies = null,
            symptoms = "Incontinência urinária, bradicardia, hipotermia, hiperestesia ao toque e oscilação corporal.",
            firstAidAdvice = "Não punir o animal. Manter em local seguro, escuro e aquecido com vigilância veterinária."
        ),
        ToxicFood(
            name = "Tulipas e Jacintos (Bolbos)",
            toxicAgent = "Tulipalina A e B",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.PLANTS,
            affectedSpecies = null,
            symptoms = "Gastroenterite hemorrágica, salivação profusa, taquicardia e depressão cardíaca.",
            firstAidAdvice = "Impedir que o cão escave ou mastigue os bolbos no jardim. Requer lavagem gástrica médica."
        ),
        ToxicFood(
            name = "Hera Inglesa (Hedera Helix)",
            toxicAgent = "Saponinas hederagénicas",
            level = ToxicityLevel.MODERATE,
            category = ToxicCategory.PLANTS,
            affectedSpecies = null,
            symptoms = "Dermatite por contacto, hipersalivação, dor abdominal e respiração arquejante.",
            firstAidAdvice = "Lavar a pele com sabão neutro e enxaguar a boca do animal com bastante água."
        ),
        ToxicFood(
            name = "Cica / Palmeira-Sagú (Cycas revoluta)",
            toxicAgent = "Cicasina",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.PLANTS,
            affectedSpecies = null,
            symptoms = "Falha hepática maciça, icterícia, melena (fezes com sangue escuro) e coagulopatias fatais.",
            firstAidAdvice = "Uma única semente pode matar um cão médio. Urgência extrema imediata!"
        ),
        ToxicFood(
            name = "Paracetamol / Acetaminofeno (Ben-u-ron / Tylenol)",
            toxicAgent = "Metahemoglobina induzida",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.MEDICATIONS,
            affectedSpecies = Species.CAT,
            symptoms = "Causa asfixia celular nos gatos (sangue castanho chocolate), edema facial e falha hepática fatal em poucas horas.",
            firstAidAdvice = "Mortal para gatos mesmo em migalhas! Antídoto N-Acetilcisteína deve ser administrado imediatamente no hospital."
        ),
        ToxicFood(
            name = "Ibuprofeno e AINEs Humanos (Aspirina, Voltaren)",
            toxicAgent = "Bloqueio de Prostaglandinas renais e gástricas",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.MEDICATIONS,
            affectedSpecies = null,
            symptoms = "Úlceras gástricas perfuradas, hematémese (vómito com sangue), falência renal aguda.",
            firstAidAdvice = "Nunca medicar animais com anti-inflamatórios humanos! Hospitalização para proteção gástrica venosa."
        ),
        ToxicFood(
            name = "Permetrina (Presente em desparasitantes caninos)",
            toxicAgent = "Neurotoxina piretroide",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.MEDICATIONS,
            affectedSpecies = Species.CAT,
            symptoms = "Tremores musculares contínuos, espasmos, hipertermia por contração e convulsões fatais em felinos.",
            firstAidAdvice = "Se aplicou pipeta de cão num gato: lave o gato imediatamente com detergente da louça morno e corra para o hospital!"
        ),
        ToxicFood(
            name = "Antidepressivos Humanos (Fluoxetina, Sertralina)",
            toxicAgent = "Síndrome Serotoninérgica",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.MEDICATIONS,
            affectedSpecies = null,
            symptoms = "Agitação extrema, tremores, rigidez muscular, taquicardia, midríase e febre alta.",
            firstAidAdvice = "Atendimento hospitalar para estabilização com antagonistas de serotonina e fluidoterapia."
        ),
        ToxicFood(
            name = "Medicamentos Cardíacos e Anti-hipertensivos",
            toxicAgent = "Bloqueadores dos canais de cálcio / Beta-bloqueadores",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.MEDICATIONS,
            affectedSpecies = null,
            symptoms = "Queda abrupta de tensão arterial (colapso), bradicardia extrema (<50 bpm) e choque cardiogénico.",
            firstAidAdvice = "Procurar hospital com cuidados intensivos e monitorização eletrocardiográfica contínua."
        ),
        ToxicFood(
            name = "Anticongelante de Radiador (Etilenoglicol)",
            toxicAgent = "Metabolitos de Etilenoglicol e Ácido Oxálico",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.CHEMICALS,
            affectedSpecies = null,
            symptoms = "Sabor adocicado que atrai pets. Causa embriaguez rápida seguida de cristais renais letais e uremia fatal.",
            firstAidAdvice = "O antídoto (Fomepizol ou Etanol farmacêutico) tem de ser administrado dentro das primeiras 3 a 5 horas!"
        ),
        ToxicFood(
            name = "Raticidas e Venenos para Roedores",
            toxicAgent = "Anticoagulantes antagonistas da Vitamina K",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.CHEMICALS,
            affectedSpecies = null,
            symptoms = "Hemorragias internas espontâneas nos pulmões, articulações e abdómen dias após a ingestão, tosse com sangue.",
            firstAidAdvice = "Levar a embalagem do veneno à clínica. O tratamento prolongado com Vitamina K1 salva a vida se iniciado cedo."
        ),
        ToxicFood(
            name = "Óleo de Melaleuca Puro (Tea Tree Oil)",
            toxicAgent = "Terpenos aromáticos concentrados",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.CHEMICALS,
            affectedSpecies = Species.CAT,
            symptoms = "Hipotermia grave, ataxia, paralisia temporária e coma por absorção cutânea em gatos.",
            firstAidAdvice = "Lavar a pele com champô suave. Evitar o uso de óleos essenciais concentrados perto de animais."
        ),
        ToxicFood(
            name = "Lixívia e Produtos de Limpeza com Cloro",
            toxicAgent = "Hipoclorito e bases cáusticas",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.CHEMICALS,
            affectedSpecies = null,
            symptoms = "Queimaduras químicas corrosivas no esófago, mucosa lingual e estômago, espasmos de dor.",
            firstAidAdvice = "NÃO induzir o vómito para não queimar novamente o esófago. Oferecer água ou caldo para diluir e ir ao hospital."
        ),
        ToxicFood(
            name = "Bolas de Naftalina (Naftaleno)",
            toxicAgent = "Naftaleno e Paradiclorobenzeno",
            level = ToxicityLevel.HIGH,
            category = ToxicCategory.CHEMICALS,
            affectedSpecies = null,
            symptoms = "Anemia hemolítica fulminante, danos hepáticos graves e convulsões.",
            firstAidAdvice = "Remover o animal do odor e levar imediatamente para descontaminação hospitalar."
        ),
        ToxicFood(
            name = "Metaldeído (Veneno para Lesmas e Caracóis)",
            toxicAgent = "Metaldeído neurotóxico",
            level = ToxicityLevel.EXTREME,
            category = ToxicCategory.CHEMICALS,
            affectedSpecies = Species.DOG,
            symptoms = "Conhecido como 'síndrome do cão que treme'. Contrações musculares violentas ininterruptas e hipertermia > 42°C.",
            firstAidAdvice = "Emergência máxima canina! Sedação imediata e lavagem gástrica sob anestesia geral no hospital."
        )
    )
}
