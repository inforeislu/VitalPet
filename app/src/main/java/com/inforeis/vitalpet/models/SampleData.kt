package com.inforeis.vitalpet.models

import java.util.Calendar
import java.util.Date

object SampleData {
    fun createSamplePets(): List<Pet> {
        val cal = Calendar.getInstance()

        // 1. Thor (Golden Retriever)
        cal.time = Date()
        cal.add(Calendar.YEAR, -3)
        val thorDob = cal.time

        val thor = Pet(
            id = "thor-golden-01",
            name = "Thor",
            species = Species.DOG,
            breed = "Golden Retriever",
            birthDate = thorDob,
            currentWeightKg = 31.5,
            gender = PetGender.MALE,
            targetWeightKg = 30.0,
            bcs = 7,
            neuterStatus = NeuterStatus.NEUTERED,
            activityLevel = ActivityLevel.NORMAL,
            notes = "Acompanhamento de perda de peso ativo. Alergia ligeira a proteína de frango.",
            microchipNumber = "941000025893412",
            passportNumber = "PT-04/987123",
            insurancePolicy = "Fidelidade Pets #874129",
            clinicContact = "+351 210 999 888",
            allergies = listOf("Frango", "Milho"),
            chronicConditions = listOf("Sensibilidade Cutânea")
        )

        // Thor Weight Logs
        cal.time = Date()
        cal.add(Calendar.MONTH, -3)
        thor.weightLogs.add(WeightLog(weightKg = 33.0, date = cal.time, bcs = 8, notes = "Início de dieta"))
        cal.time = Date()
        cal.add(Calendar.MONTH, -1)
        thor.weightLogs.add(WeightLog(weightKg = 32.2, date = cal.time, bcs = 7))
        thor.weightLogs.add(WeightLog(weightKg = 31.5, date = Date(), bcs = 7))

        // Thor Diet Plan
        thor.dietPlans.add(
            DietPlan(
                foodName = "Ração Vet Care Weight Loss",
                kcalPer100g = 340.0,
                targetDailyKcal = thor.der
            )
        )

        // Thor Symptoms
        cal.time = Date()
        cal.add(Calendar.DAY_OF_YEAR, -4)
        thor.symptomLogs.add(
            SymptomLog(
                date = cal.time,
                fecalScore = 3,
                vomitingEpisodes = 0,
                appetite = AppetiteLevel.NORMAL,
                energyLevel = EnergyLevel.NORMAL,
                waterIntake = WaterIntakeLevel.NORMAL,
                notes = "Fezes ligeiramente mais húmidas."
            )
        )
        cal.time = Date()
        cal.add(Calendar.DAY_OF_YEAR, -2)
        thor.symptomLogs.add(
            SymptomLog(
                date = cal.time,
                fecalScore = 2,
                vomitingEpisodes = 0,
                appetite = AppetiteLevel.NORMAL,
                energyLevel = EnergyLevel.HIGH,
                waterIntake = WaterIntakeLevel.NORMAL,
                notes = "Consistência perfeita, muito enérgico."
            )
        )

        // Thor Walks
        cal.time = Date()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        thor.walkSessions.add(
            WalkSession(
                date = cal.time,
                durationSeconds = 2100,
                distanceMeters = 2300.0,
                caloriesBurned = 85.0,
                intensity = WalkIntensity.MODERATE
            )
        )

        // Thor Fasting
        cal.time = Date()
        cal.add(Calendar.HOUR_OF_DAY, 7)
        thor.fastingLogs.add(
            FastingLog(
                procedure = FastingProcedure.GENERAL_SURGERY,
                procedureDate = cal.time,
                foodFastingHours = 8,
                waterFastingHours = 2
            )
        )

        // Thor Vaccines
        cal.time = Date()
        cal.add(Calendar.MONTH, -6)
        val vDate = cal.time
        cal.add(Calendar.MONTH, 6)
        thor.vaccineLogs.add(
            VaccineLog(
                vaccineName = "Polivalente Canina DHPPi+L",
                applicationDate = vDate,
                nextDueDate = cal.time,
                batchNumber = "VX-98214"
            )
        )

        // 2. Luna (Siamese Cat)
        cal.time = Date()
        cal.add(Calendar.YEAR, -2)
        val lunaDob = cal.time

        val luna = Pet(
            id = "luna-siamese-02",
            name = "Luna",
            species = Species.CAT,
            breed = "Siamês",
            birthDate = lunaDob,
            currentWeightKg = 4.1,
            gender = PetGender.FEMALE,
            targetWeightKg = 4.0,
            bcs = 5,
            neuterStatus = NeuterStatus.NEUTERED,
            activityLevel = ActivityLevel.SEDENTARY,
            notes = "Animal com excelente condição biológica.",
            microchipNumber = "941000018472910",
            passportNumber = "PT-04/112456",
            insurancePolicy = "Mapfre Animal #339102",
            clinicContact = "+351 210 999 888",
            allergies = emptyList(),
            chronicConditions = emptyList()
        )

        // Luna Weight Logs
        cal.time = Date()
        cal.add(Calendar.MONTH, -2)
        luna.weightLogs.add(WeightLog(weightKg = 4.3, date = cal.time, bcs = 6))
        luna.weightLogs.add(WeightLog(weightKg = 4.1, date = Date(), bcs = 5))

        // Luna Diet Plan
        luna.dietPlans.add(
            DietPlan(
                foodName = "Ração Sterilised Cat Indoor",
                kcalPer100g = 350.0,
                targetDailyKcal = luna.der
            )
        )

        // Luna Symptoms
        cal.time = Date()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        luna.symptomLogs.add(
            SymptomLog(
                date = cal.time,
                fecalScore = 2,
                vomitingEpisodes = 0,
                appetite = AppetiteLevel.NORMAL,
                energyLevel = EnergyLevel.NORMAL,
                waterIntake = WaterIntakeLevel.NORMAL,
                notes = "Dia tranquilo e apetite saudável."
            )
        )

        return listOf(thor, luna)
    }
}
