package com.inforeis.vitalpet

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.services.LanguageManager
import com.inforeis.vitalpet.services.PetRepository
import com.inforeis.vitalpet.ui.theme.VitalPetTheme
import com.inforeis.vitalpet.ui.views.AddEditPetScreen
import com.inforeis.vitalpet.ui.views.AgeConversionScreen
import com.inforeis.vitalpet.ui.views.BCSEvaluatorScreen
import com.inforeis.vitalpet.ui.views.BarfCalculatorScreen
import com.inforeis.vitalpet.ui.views.BreedPickerScreen
import com.inforeis.vitalpet.ui.views.ClinicalFastingTimerScreen
import com.inforeis.vitalpet.ui.views.HydrationCalculatorScreen
import com.inforeis.vitalpet.ui.views.MainScreen
import com.inforeis.vitalpet.ui.views.MedicationDoseCalculatorScreen
import com.inforeis.vitalpet.ui.views.OnboardingScreen
import com.inforeis.vitalpet.ui.views.PetDigitalIDCardScreen
import com.inforeis.vitalpet.ui.views.PetDetailScreen
import com.inforeis.vitalpet.ui.views.PetFoodLabelAnalyzerScreen
import com.inforeis.vitalpet.ui.views.SplashScreen
import com.inforeis.vitalpet.ui.views.ToxicFoodGuideScreen
import com.inforeis.vitalpet.ui.views.FoodTransitionScreen
import com.inforeis.vitalpet.ui.views.PetRecipesScreen
import com.inforeis.vitalpet.ui.views.VeterinaryEmergencyMapScreen
import com.inforeis.vitalpet.ui.views.WalkTrackerScreen
import com.inforeis.vitalpet.ui.views.PremiumPaywallScreen
import com.inforeis.vitalpet.ui.views.HealthEvolutionChartsScreen
import com.inforeis.vitalpet.ui.views.BeforeAfterComparisonScreen
import com.inforeis.vitalpet.ui.views.BreedEncyclopediaScreen
import com.inforeis.vitalpet.ui.views.BreedDetailScreen
import com.inforeis.vitalpet.ui.views.BreedComparisonScreen
import com.inforeis.vitalpet.ui.views.BreedMatchQuizScreen
import com.inforeis.vitalpet.ui.views.DesktopSyncScreen
import com.inforeis.vitalpet.ui.views.SymptomJournalScreen
import com.inforeis.vitalpet.services.BreedDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by com.inforeis.vitalpet.services.ThemeManager.currentThemeMode.collectAsState()
            VitalPetTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val routeExtra = intent?.getStringExtra("extra_route")
                    VitalPetAppRoot(initialRoute = routeExtra)
                }
            }
        }
    }
}

@Composable
fun VitalPetAppRoot(initialRoute: String? = null) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("vitalpet_prefs", Context.MODE_PRIVATE) }
    val currentLang by LanguageManager.currentLanguage.collectAsState()
    var isSplashFinished by remember { mutableStateOf(false) }
    var hasCompletedOnboarding by remember {
        mutableStateOf(prefs.getBoolean("has_completed_onboarding", false))
    }

    val navController = rememberNavController()

    androidx.compose.runtime.LaunchedEffect(isSplashFinished, hasCompletedOnboarding) {
        if (isSplashFinished && hasCompletedOnboarding && initialRoute == "digital_id") {
            val firstPet = PetRepository.pets.value.firstOrNull()
            if (firstPet != null) {
                navController.navigate("digital_id/${firstPet.id}")
            }
        }
    }

    androidx.compose.runtime.key(currentLang) {
        if (!isSplashFinished) {
            SplashScreen(onFinish = { isSplashFinished = true })
        } else if (!hasCompletedOnboarding) {
            OnboardingScreen(
                onComplete = {
                    prefs.edit().putBoolean("has_completed_onboarding", true).apply()
                    hasCompletedOnboarding = true
                }
            )
        } else {
            NavHost(navController = navController, startDestination = "main") {
            composable("main") {
                MainScreen(
                    onSelectPet = { pet ->
                        navController.navigate("pet_detail/${pet.id}")
                    },
                    onAddPet = {
                        navController.navigate("add_pet")
                    },
                    onEditPet = { pet ->
                        navController.navigate("edit_pet/${pet.id}")
                    },
                    onNavigateUtility = { route ->
                        navController.navigate(route)
                    },
                    onOpenToxicGuide = {
                        navController.navigate("toxic_guide")
                    },
                    onOpenEmergencyMap = {
                        navController.navigate("emergency_map")
                    },
                    onOpenPaywall = {
                        navController.navigate("paywall")
                    }
                )
            }
            composable("pet_detail/{petId}") { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId") ?: ""
                PetDetailScreen(
                    petId = petId,
                    onBack = { navController.popBackStack() },
                    onOpenDigitalId = { pet ->
                        navController.navigate("digital_id/${pet.id}")
                    },
                    onOpenHealthCharts = { id ->
                        navController.navigate("health_charts/$id")
                    },
                    onOpenEvolutionPhotos = { id ->
                        navController.navigate("evolution_photos/$id")
                    },
                    onOpenSymptomJournal = { id ->
                        navController.navigate("symptom_journal/$id")
                    }
                )
            }
            composable("add_pet") {
                AddEditPetScreen(
                    petId = null,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("edit_pet/{petId}") { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId")
                AddEditPetScreen(
                    petId = petId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("digital_id/{petId}") { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId") ?: ""
                val pet = PetRepository.getPetById(petId)
                if (pet != null) {
                    PetDigitalIDCardScreen(
                        pet = pet,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable("recipes") {
                PetRecipesScreen(onBack = { navController.popBackStack() })
            }
            composable("food_transition") {
                FoodTransitionScreen(onBack = { navController.popBackStack() })
            }
            composable("barf") {
                BarfCalculatorScreen(onBack = { navController.popBackStack() })
            }
            composable("label_analyzer") {
                PetFoodLabelAnalyzerScreen(onBack = { navController.popBackStack() })
            }
            composable("med_calculator") {
                MedicationDoseCalculatorScreen(onBack = { navController.popBackStack() })
            }
            composable("hydration") {
                HydrationCalculatorScreen(onBack = { navController.popBackStack() })
            }
            composable("fasting_timer") {
                ClinicalFastingTimerScreen(onBack = { navController.popBackStack() })
            }
            composable("walk_tracker") {
                WalkTrackerScreen(onBack = { navController.popBackStack() })
            }
            composable("age_conversion") {
                AgeConversionScreen(onBack = { navController.popBackStack() })
            }
            composable("bcs_evaluator") {
                BCSEvaluatorScreen(onBack = { navController.popBackStack() })
            }
            composable("breed_db") {
                BreedPickerScreen(onBack = { navController.popBackStack() })
            }
            composable("health_charts") {
                HealthEvolutionChartsScreen(initialPetId = null, onBack = { navController.popBackStack() })
            }
            composable("health_charts/{petId}") { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId")
                HealthEvolutionChartsScreen(initialPetId = petId, onBack = { navController.popBackStack() })
            }
            composable("evolution_photos") {
                BeforeAfterComparisonScreen(initialPetId = null, onBack = { navController.popBackStack() })
            }
            composable("evolution_photos/{petId}") { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId")
                BeforeAfterComparisonScreen(initialPetId = petId, onBack = { navController.popBackStack() })
            }
            composable("symptom_journal") {
                SymptomJournalScreen(initialPetId = null, onBack = { navController.popBackStack() })
            }
            composable("symptom_journal/{petId}") { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId")
                SymptomJournalScreen(initialPetId = petId, onBack = { navController.popBackStack() })
            }
            composable("breed_encyclopedia") {
                BreedEncyclopediaScreen(
                    onBack = { navController.popBackStack() },
                    onOpenQuiz = { navController.navigate("breed_quiz") },
                    onOpenComparison = { id -> navController.navigate(if (id != null) "breed_comparison/$id" else "breed_comparison") },
                    onSelectBreedDetail = { breed -> navController.navigate("breed_detail/${breed.id}") }
                )
            }
            composable("breed_detail/{breedId}") { backStackEntry ->
                val breedId = backStackEntry.arguments?.getString("breedId")
                val breed = BreedDatabase.allBreeds.find { it.id == breedId } ?: BreedDatabase.allBreeds.first()
                BreedDetailScreen(
                    breed = breed,
                    onBack = { navController.popBackStack() },
                    onCompare = { b -> navController.navigate("breed_comparison/${b.id}") }
                )
            }
            composable("breed_quiz") {
                BreedMatchQuizScreen(
                    onBack = { navController.popBackStack() },
                    onSelectBreed = { breed -> navController.navigate("breed_detail/${breed.id}") }
                )
            }
            composable("breed_comparison") {
                BreedComparisonScreen(initialBreedId = null, onBack = { navController.popBackStack() })
            }
            composable("breed_comparison/{breedId}") { backStackEntry ->
                val breedId = backStackEntry.arguments?.getString("breedId")
                BreedComparisonScreen(initialBreedId = breedId, onBack = { navController.popBackStack() })
            }
            composable("toxic_guide") {
                ToxicFoodGuideScreen(onBack = { navController.popBackStack() })
            }
            composable("emergency_map") {
                VeterinaryEmergencyMapScreen(onBack = { navController.popBackStack() })
            }
            composable("paywall") {
                PremiumPaywallScreen(onDismiss = { navController.popBackStack() })
            }
            composable("onboarding") {
                OnboardingScreen(
                    onComplete = {
                        prefs.edit().putBoolean("has_completed_onboarding", true).apply()
                        navController.popBackStack()
                    }
                )
            }
            composable("desktop_sync") {
                DesktopSyncScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
}


