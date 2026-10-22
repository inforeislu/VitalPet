import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    jvmToolchain(21)
}

val syncSharedSources by tasks.registering(Sync::class) {
    from("../app/src/main/java") {
        include("com/inforeis/vitalpet/models/**")
        include("com/inforeis/vitalpet/services/BreedDatabase.kt")
        include("com/inforeis/vitalpet/services/ToxicFoodDatabase.kt")
        include("com/inforeis/vitalpet/services/EmergencyProtocolsDatabase.kt")
        include("com/inforeis/vitalpet/services/PetRecipesDatabase.kt")
        include("com/inforeis/vitalpet/services/ClinicalCalculatorService.kt")
        include("com/inforeis/vitalpet/services/VitalPetJson.kt")
    }
    into(layout.buildDirectory.dir("generated/sharedSources"))
}

sourceSets {
    main {
        kotlin {
            srcDir("src/main/kotlin")
            srcDir(layout.buildDirectory.dir("generated/sharedSources"))
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn(syncSharedSources)
}

tasks.named<ProcessResources>("processResources") {
    from("../app/src/main/assets") {
        include("translations.json")
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.components.resources)

    implementation(libs.gson)
    implementation(libs.zxing.core)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    testImplementation(libs.junit)
}

compose.desktop {
    application {
        mainClass = "com.inforeis.vitalpet.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe)
            packageName = "VitalPet"
            packageVersion = "1.0.0"
            description = "VitalPet - Gestão Clínica e Nutricional Veterinária"
            vendor = "InfoReis"
            copyright = "© 2026 InfoReis. All rights reserved."

            windows {
                menuGroup = "VitalPet"
                shortcut = true
                upgradeUuid = "a48b7f80-0a21-4f36-a364-bb23faefb598"
                iconFile.set(project.file("src/main/resources/icon.ico"))
            }
        }
    }
}
