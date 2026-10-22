package com.inforeis.vitalpet

import com.inforeis.vitalpet.models.ActivityLevel
import com.inforeis.vitalpet.models.NeuterStatus
import com.inforeis.vitalpet.models.PetGender
import com.inforeis.vitalpet.models.Species
import com.inforeis.vitalpet.services.BackupManager
import com.inforeis.vitalpet.services.VitalPetJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class CrossPlatformBackupTest {

    @Test
    fun testParseIosSwiftPayloadWithIso8601DatesAndLowercaseEnums() {
        val iosJson = """
        {
          "app": "VitalPet",
          "version": "1.0",
          "exportDate": "2026-09-29T08:30:00Z",
          "pets": [
            {
              "id": "11111111-2222-3333-4444-555555555555",
              "name": "Luna",
              "species": "cat",
              "breed": "Siamês",
              "birthDate": "2021-04-15T00:00:00.000Z",
              "currentWeightKg": 4.25,
              "gender": "female",
              "targetWeightKg": 4.0,
              "bcs": 5,
              "neuterStatus": "neutered",
              "activityLevel": "normal",
              "weightLogs": [
                {
                  "id": "w-1",
                  "weightKg": 4.10,
                  "date": "2026-01-10T10:00:00Z"
                },
                {
                  "id": "w-2",
                  "weightKg": 4.25,
                  "date": "2026-02-15T10:00:00Z"
                }
              ],
              "allergies": ["Frango"],
              "chronicConditions": []
            }
          ]
        }
        """.trimIndent()

        val parsed = BackupManager.parsePetsFromJson(iosJson)
        assertEquals(1, parsed.size)

        val pet = parsed[0]
        assertEquals("Luna", pet.name)
        assertEquals(Species.CAT, pet.species)
        assertEquals(PetGender.FEMALE, pet.gender)
        assertEquals(NeuterStatus.NEUTERED, pet.neuterStatus)
        assertEquals(ActivityLevel.NORMAL, pet.activityLevel)
        assertEquals(4.25, pet.currentWeightKg, 0.01)

        val cal = Calendar.getInstance()
        cal.time = pet.birthDate
        assertEquals(2021, cal.get(Calendar.YEAR))

        assertEquals(2, pet.weightLogs.size)
        assertEquals(4.10, pet.weightLogs[0].weightKg, 0.01)
        assertEquals(4.25, pet.weightLogs[1].weightKg, 0.01)
        assertNotNull(pet.fastingLogs)
        assertNotNull(pet.symptomLogs)
        assertNotNull(pet.evolutionPhotos)
    }

    @Test
    fun testParseDirectArrayFormat() {
        val arrayJson = """
        [
          {
            "id": "22222222-3333-4444-5555-666666666666",
            "name": "Thor",
            "species": "dog",
            "breed": "Golden Retriever",
            "birthDate": "2020-08-20T12:00:00Z",
            "currentWeightKg": 32.5,
            "gender": "male",
            "bcs": 5,
            "neuterStatus": "intact",
            "activityLevel": "active"
          }
        ]
        """.trimIndent()

        val parsed = BackupManager.parsePetsFromJson(arrayJson)
        assertEquals(1, parsed.size)

        val pet = parsed[0]
        assertEquals("Thor", pet.name)
        assertEquals(Species.DOG, pet.species)
        assertEquals(PetGender.MALE, pet.gender)
        assertEquals(NeuterStatus.INTACT, pet.neuterStatus)
        assertEquals(ActivityLevel.ACTIVE, pet.activityLevel)
        assertEquals(32.5, pet.currentWeightKg, 0.01)
    }

    @Test
    fun testSerializationRoundtripOutputsIso8601() {
        val pets = com.inforeis.vitalpet.models.SampleData.createSamplePets()
        val json = VitalPetJson.gson.toJson(pets)
        assertTrue(json.contains("\"birthDate\": \""))
        assertTrue(json.contains("Z\"")) // ISO 8601 UTC indicator

        val reimported = BackupManager.parsePetsFromJson(json)
        assertEquals(pets.size, reimported.size)
        assertEquals(pets[0].name, reimported[0].name)
    }
}
