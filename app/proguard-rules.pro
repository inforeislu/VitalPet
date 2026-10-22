# VitalPet Android — ProGuard / R8 Rules for Release Builds

# Keep Kotlin Reflection and Metadata
-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preserve VitalPet Data Models & Enums for Gson serialization
-keep class com.inforeis.vitalpet.models.** { *; }
-keepclassmembers class com.inforeis.vitalpet.models.** { *; }

# Preserve VitalPet Services, Repositories and Serialization
-keep class com.inforeis.vitalpet.services.VitalPetJson** { *; }
-keep class com.inforeis.vitalpet.services.BackupManager** { *; }
-keep class com.inforeis.vitalpet.services.BackupPayload** { *; }
-keep class com.inforeis.vitalpet.services.DataIntegrityReport** { *; }
-keep class com.inforeis.vitalpet.services.PetRepository** { *; }
-keep class com.inforeis.vitalpet.services.PetQuickWidgetProvider** { *; }
-keep class com.inforeis.vitalpet.services.ClinicalAlarmReceiver** { *; }
-keep class com.inforeis.vitalpet.services.ClinicalNotificationManager** { *; }

# Gson Rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# ZXing Rules
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# Jetpack Compose Rules
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Google Play Billing Rules
-keep class com.android.billingclient.api.** { *; }
-dontwarn com.android.billingclient.api.**

