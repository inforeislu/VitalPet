package com.inforeis.vitalpet.services

import com.inforeis.vitalpet.models.EmergencyCategory
import com.inforeis.vitalpet.models.EmergencyProtocol
import com.inforeis.vitalpet.models.UrgencyLevel

object EmergencyProtocolsDatabase {
    val protocols: List<EmergencyProtocol> = listOf(
        EmergencyProtocol(
            id = "cpr-dog-cat",
            title = "Reanimação Cardiorrespiratória (RCP)",
            category = EmergencyCategory.CPR,
            urgency = UrgencyLevel.CRITICAL,
            symptoms = listOf(
                "Ausência de respiração voluntária",
                "Sem pulso detetável na artéria femoral",
                "Gengivas acinzentadas, azuis ou brancas",
                "Pupilas dilatadas e sem reflexo"
            ),
            steps = listOf(
                "Deite o animal sobre o seu lado direito em superfície firme.",
                "Estenda o pescoço e retire saliva ou corpos estranhos da boca.",
                "Posicione as mãos sobre a área cardíaca (atrás da articulação do cotovelo).",
                "Comprima com ritmo de 100 a 120 compressões por minuto (profundidade de 1/3 do diâmetro torácico).",
                "Mantenha rácio de 30 compressões para 2 insuflações na boca/focinho fechado.",
                "Transporte imediatamente para o hospital mantendo as manobras."
            ),
            doNotDo = listOf(
                "Não perca tempo a tentar ouvir o coração se o animal estiver inconsciente.",
                "Não faça compressões torácicas se o animal demonstrar consciência ou resistência.",
                "Não comprima o abdómen em vez do tórax."
            )
        ),
        EmergencyProtocol(
            id = "choking-obstruction",
            title = "Asfixia por Corpo Estranho",
            category = EmergencyCategory.CPR,
            urgency = UrgencyLevel.CRITICAL,
            symptoms = listOf(
                "Engasgo violento e tentativas de vómito sem sucesso",
                "Patas na boca com desespero",
                "Ruído respiratório agudo ou silêncio absoluto",
                "Língua arroxeada por falta de oxigénio"
            ),
            steps = listOf(
                "Abra a boca do animal com cuidado para inspecionar visualmente a garganta.",
                "Se o objeto for visível e estiver solto, tente removê-lo com os dedos ou pinça.",
                "Para cães médios/grandes: posicione os punhos logo abaixo da caixa torácica e aplique 3 a 5 impulsos firmes para a frente (Heimlich animal).",
                "Para gatos e cães pequenos: eleve os membros posteriores com cuidado para ajudar a gravidade.",
                "Dirija-se ao hospital mais próximo imediatamente com via aérea monitorizada."
            ),
            doNotDo = listOf(
                "Não tente agarrar objetos às cegas para não os empurrar mais fundo na traqueia.",
                "Não aplique força excessiva que possa fraturar costelas em animais miniatura."
            )
        ),
        EmergencyProtocol(
            id = "heatstroke",
            title = "Golpe de Calor (Hipertermia Severa)",
            category = EmergencyCategory.ENVIRONMENT,
            urgency = UrgencyLevel.CRITICAL,
            symptoms = listOf(
                "Arfar frenético e respiração muito acelerada",
                "Salivação espessa e pegajosa",
                "Temperatura retal superior a 40.5 °C",
                "Fraqueza nas patas traseiras, desorientação ou desmaio"
            ),
            steps = listOf(
                "Retire o animal do sol ou do local fechado imediatamente para ambiente fresco e ventilado.",
                "Molhe o animal com água fresca ou tépida (nunca gelada).",
                "Coloque toalhas húmidas na virilha, axilas e almofadinhas das patas.",
                "Ligue a ventoinha ou o ar condicionado virado para o animal.",
                "Pare o arrefecimento ativo quando a temperatura atingir 39.5 °C para evitar hipotermia reflexa."
            ),
            doNotDo = listOf(
                "NUNCA use gelo ou água gelada, pois provoca vasoconstrição periférica retendo o calor interno.",
                "Não force o animal a beber grandes quantidades de água de uma vez só."
            )
        ),
        EmergencyProtocol(
            id = "poisoning-toxicity",
            title = "Intoxicação & Envenenamento Agudo",
            category = EmergencyCategory.TOXICITY,
            urgency = UrgencyLevel.CRITICAL,
            symptoms = listOf(
                "Salivação excessiva (hipersalivação espumosa)",
                "Vómitos agudos ou diarreia com odor forte ou sangue",
                "Tremores musculares contínuos ou convulsões",
                "Pupilas contraídas ou amplamente dilatadas"
            ),
            steps = listOf(
                "Afaste imediatamente o pet da fonte tóxica.",
                "Identifique o agente ingerido (guarde o frasco, planta ou embalagem para mostrar ao veterinário).",
                "Se foi contacto cutâneo, lave a pele do animal com água morna e sabão neutro.",
                "Ligue para a linha de urgência veterinária enquanto se desloca para o hospital."
            ),
            doNotDo = listOf(
                "NÃO induza o vómito se a substância for cáustica, corrosiva, produto de limpeza ou derivado de petróleo.",
                "NÃO dê leite ou azeite, pois aceleram a absorção de várias toxinas lipossolúveis.",
                "Não perca tempo com receitas caseiras."
            )
        )
    )
}
