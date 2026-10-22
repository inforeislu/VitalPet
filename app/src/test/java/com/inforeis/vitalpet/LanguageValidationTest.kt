package com.inforeis.vitalpet

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inforeis.vitalpet.models.ActivityLevel
import com.inforeis.vitalpet.models.NeuterStatus
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.models.PetGender
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.services.ClinicalCalculatorService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.Date

class LanguageValidationTest {

    private val requiredLanguages = listOf("pt", "en", "es", "fr", "de", "it", "nl", "lb")

    @Test
    fun testMandatoryEightLanguagesCompliance() {
        val jsonFile = File("src/main/assets/translations.json")
        assertTrue("translations.json deve existir em assets!", jsonFile.exists())

        val content = jsonFile.readText()
        val type = object : TypeToken<Map<String, Map<String, String>>>() {}.type
        val translations: Map<String, Map<String, String>> = Gson().fromJson(content, type)

        assertTrue("translations.json deve conter termos traduzidos!", translations.isNotEmpty())

        val missingReport = mutableListOf<String>()

        for ((termKey, langMap) in translations) {
            for (lang in requiredLanguages) {
                if (!langMap.containsKey(lang)) {
                    missingReport.add("Termo '$termKey' não possui chave para o idioma '$lang'.")
                } else {
                    val value = langMap[lang]
                    if (value.isNullOrBlank()) {
                        missingReport.add("Termo '$termKey' possui tradução vazia para o idioma '$lang'.")
                    }
                }
            }
        }

        assertTrue(
            "Encontradas ${missingReport.size} falhas de tradução nos 8 idiomas obrigatórios:\n" +
                    missingReport.take(10).joinToString("\n"),
            missingReport.isEmpty()
        )
    }

    @Test
    fun testWSAVACalculations() {
        val thor = Pet(
            name = "Thor",
            species = Species.DOG,
            breed = "Golden Retriever",
            birthDate = Date(),
            currentWeightKg = 31.5,
            gender = PetGender.MALE,
            neuterStatus = NeuterStatus.NEUTERED,
            activityLevel = ActivityLevel.NORMAL
        )

        // RER = 70 * (31.5 ^ 0.75) ≈ 931 kcal
        assertTrue("RER deve ser positivo e superior a 800 kcal para 31.5 kg", thor.rer in 900.0..950.0)

        // Factor cão castrado manutenção = 1.6 -> DER ≈ 1490 kcal
        assertTrue("DER deve estar de acordo com o fator 1.6", thor.der in 1400.0..1600.0)
    }

    @Test
    fun testClinicalCalculators() {
        // BARF 20 kg cão 2.5% -> 500 g/dia
        val barf = ClinicalCalculatorService.calculateBarf(20.0, 2.5, false)
        assertEquals(500.0, barf.totalDailyGrams, 0.1)
        assertEquals(250.0, barf.muscleMeatGrams, 0.1) // 50%
        assertEquals(85.0, barf.rawMeatyBonesGrams, 0.1) // 17%

        // Análise de rótulo
        val analysis = ClinicalCalculatorService.analyzeFoodLabel(26.0, 14.0, 3.0, 10.0, 7.0)
        assertEquals(90.0, analysis.dryMatterPercent, 0.1)
        assertTrue(analysis.dryMatterCarbs > 0)
    }
}
