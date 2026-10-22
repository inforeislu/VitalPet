package com.inforeis.vitalpet.services

import com.inforeis.vitalpet.models.PetRecipe
import com.inforeis.vitalpet.models.RecipeCategory
import com.inforeis.vitalpet.models.Species

object PetRecipesDatabase {
    val recipes: List<PetRecipe> = listOf(
        PetRecipe(
            id = "rec-chicken-rice-bland",
            title = "Dieta Branda Clássica (Frango & Arroz)",
            category = RecipeCategory.BLAND_DIET,
            species = Species.DOG,
            prepTimeMinutes = 25,
            portions = "2 a 3 refeições",
            ingredients = listOf(
                "250g de peito de frango limpo sem osso e sem gordura visível",
                "150g de arroz carolino ou agulha cozido muito além do ponto habitual",
                "100g de cenoura raspada ou abóbora manteiga cozida e esmagada",
                "Água da cozedura (rica em colagénio e minerais hidratantes)",
                "1 pitada de sal marinho fino (apenas se indicado pelo médico veterinário)"
            ),
            instructions = listOf(
                "Coza o peito de frango apenas em água, sem temperos, azeite ou cebola/alho (tóxicos).",
                "Em panela separada, coza o arroz até abrir completamente e soltar a goma natural.",
                "Coza a abóbora ou cenoura ao vapor até ficar macia e fácil de reduzir a puré.",
                "Desfie finamente o peito de frango já arrefecido.",
                "Misture na proporção recomendada: 2 partes de arroz cozido para 1 parte de frango desfiado.",
                "Adicione 2 a 3 colheres do puré vegetal e uma concha do caldo da cozedura morno.",
                "Sirva sempre morno ou à temperatura ambiente, nunca saído do frigorífico."
            ),
            nutritionalNotes = "Indicado para alívio temporário de gastroenterites agudas, diarreia transitória e pós-cirurgia."
        ),
        PetRecipe(
            id = "rec-bone-broth",
            title = "Caldo Dourado de Ossos & Curcuma",
            category = RecipeCategory.RENAL_SUPPORT,
            species = Species.DOG,
            prepTimeMinutes = 45,
            portions = "10 doses diárias",
            ingredients = listOf(
                "1 kg de ossos com cartilagem (joelho ou mocotó bovino)",
                "2 colheres de sopa de vinagre de sidra natural",
                "1 cenoura inteira biológica e 1 talo de aipo limpo",
                "1 colher de café rasa de curcuma pura moída",
                "2 litros de água filtrada"
            ),
            instructions = listOf(
                "Coloque os ossos na panela com água filtrada cobrindo 5 cm acima do topo.",
                "Adicione o vinagre de sidra (essencial para extrair cálcio e minerais dos ossos).",
                "Deixe ferver e em seguida reduza para lume brando durante 6 a 12 horas.",
                "Na última hora, adicione os vegetais e a curcuma.",
                "Coe o caldo com rede fina removendo TODOS os ossos com extremo rigor. NUNCA dê ossos cozidos ao pet!",
                "Deixe arrefecer no frigorífico e descarte a camada sólida de gordura na superfície.",
                "Congele em cuvetes de gelo para oferecer diariamente como reforço de hidratação."
            ),
            nutritionalNotes = "Rico em colagénio, glucosamina, prolina e glicina para proteção articular e renal."
        ),
        PetRecipe(
            id = "rec-cat-salmon-snack",
            title = "Snack Húmido de Salmão & Abóbora para Gatos",
            category = RecipeCategory.HEALTHY_TREAT,
            species = Species.CAT,
            prepTimeMinutes = 20,
            portions = "4 doses",
            ingredients = listOf(
                "150g de lombo de salmão fresco cozido ao vapor sem espinhas",
                "50g de puré de abóbora cozida sem casca",
                "1 colher de café de óleo de peixe ômega-3",
                "Água morna para textura suave"
            ),
            instructions = listOf(
                "Coza o salmão suavemente ao vapor até desfiar facilmente.",
                "Verifique minuciosamente com os dedos a ausência absoluta de qualquer espinha.",
                "Bata no liquidificador ou com varinha mágica o salmão, o puré de abóbora e um pouco de água morna.",
                "Adicione o óleo ômega-3 e misture suavemente.",
                "Ofereça como snack ou topper na ração habitual para incentivar apetite ou hidratação."
            ),
            nutritionalNotes = "A fibra solúvel da abóbora auxilia na digestão e combate bolas de pelo felinas."
        )
    )
}
