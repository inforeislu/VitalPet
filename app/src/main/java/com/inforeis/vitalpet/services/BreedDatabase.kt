package com.inforeis.vitalpet.services

import com.inforeis.vitalpet.models.DogSizeCategory
import com.inforeis.vitalpet.models.PetBreed
import com.inforeis.vitalpet.models.Species

object BreedDatabase {
    val allBreeds: List<PetBreed> get() = breeds
    val breeds: List<PetBreed> = listOf(
        // Cães
        PetBreed(
            id = "dog_srd",
            name = "Sem Raça Definida (SRD)",
            species = Species.DOG,
            sizeCategory = DogSizeCategory.MEDIUM,
            minWeightKg = 10.0,
            maxWeightKg = 25.0,
            description = "Cães mestiços únicos, conhecidos pela grande rusticidade, resistência genética e adaptabilidade.",
            healthRisks = listOf("Variável conforme herança genética")
        ),
        PetBreed(
            id = "dog_golden",
            name = "Golden Retriever",
            species = Species.DOG,
            sizeCategory = DogSizeCategory.LARGE,
            minWeightKg = 25.0,
            maxWeightKg = 34.0,
            description = "Inteligente, paciente e afetuoso. Propenso a apetite voraz e displasia.",
            healthRisks = listOf("Dermatites", "Displasia da Anca", "Cataratas")
        ),
        PetBreed(
            id = "dog_labrador",
            name = "Labrador Retriever",
            species = Species.DOG,
            sizeCategory = DogSizeCategory.LARGE,
            minWeightKg = 25.0,
            maxWeightKg = 36.0,
            description = "Extremamente dócil e companheiro. Propenso ao ganho excessivo de peso com idade.",
            healthRisks = listOf("Displasia da Anca", "Obesidade", "Otites")
        ),
        PetBreed(
            id = "dog_french_bulldog",
            name = "Buldogue Francês",
            species = Species.DOG,
            sizeCategory = DogSizeCategory.SMALL,
            minWeightKg = 8.0,
            maxWeightKg = 14.0,
            description = "Afetuoso, cómico e leal. Cão braquicefálico que exige cuidados especiais com temperatura e vias aéreas.",
            healthRisks = listOf("Síndrome Braquicefálica", "Problemas de Coluna", "Dermatites de Pregas")
        ),
        PetBreed(
            id = "dog_german_shepherd",
            name = "Pastor Alemão",
            species = Species.DOG,
            sizeCategory = DogSizeCategory.LARGE,
            minWeightKg = 26.0,
            maxWeightKg = 40.0,
            description = "Vigilante, protetor e altamente inteligente. Necessita de estimulação física e mental contínua.",
            healthRisks = listOf("Displasia Coxofemoral", "Sensibilidade Digestiva")
        ),
        PetBreed(
            id = "dog_chihuahua",
            name = "Chihuahua",
            species = Species.DOG,
            sizeCategory = DogSizeCategory.SMALL,
            minWeightKg = 1.5,
            maxWeightKg = 3.0,
            description = "Compacto, enérgico e com enorme personalidade. Necessita de proteção contra o frio e hipoglicemia.",
            healthRisks = listOf("Luxação da Rótula", "Colapso de Traqueia", "Problemas Dentários")
        ),

        // Gatos
        PetBreed(
            id = "cat_srd",
            name = "Europeu Comum (SRD)",
            species = Species.CAT,
            sizeCategory = DogSizeCategory.SMALL,
            minWeightKg = 3.5,
            maxWeightKg = 5.5,
            description = "Gato equilibrado, curioso, independente e muito resistente a doenças hereditárias.",
            healthRisks = listOf("Doença Renal Crónica em idade sénior")
        ),
        PetBreed(
            id = "cat_siamese",
            name = "Siamês",
            species = Species.CAT,
            sizeCategory = DogSizeCategory.SMALL,
            minWeightKg = 3.0,
            maxWeightKg = 4.8,
            description = "Extremamente vocal, afetuoso e expressivo. Cria laços profundos com a família humana.",
            healthRisks = listOf("Asma Felina", "Gengivite", "Amiloidose")
        ),
        PetBreed(
            id = "cat_maine_coon",
            name = "Maine Coon",
            species = Species.CAT,
            sizeCategory = DogSizeCategory.MEDIUM,
            minWeightKg = 5.5,
            maxWeightKg = 10.0,
            description = "O gigante gentil do mundo felino. Muito sociável, adora água e tem pelagem densa resistente ao frio.",
            healthRisks = listOf("Cardiomiopatia Hipertrófica (CMH)", "Displasia de Anca")
        ),
        PetBreed(
            id = "cat_persian",
            name = "Persa",
            species = Species.CAT,
            sizeCategory = DogSizeCategory.SMALL,
            minWeightKg = 3.5,
            maxWeightKg = 6.0,
            description = "Calmo, doce e aristocrático. Exige escovagem diária e higienização dos olhos devido à face plana.",
            healthRisks = listOf("Rim Policístico (PKD)", "Problemas Respiratórios")
        )
    )

    fun getBreedsForSpecies(species: Species): List<PetBreed> = breeds.filter { it.species == species }
}
