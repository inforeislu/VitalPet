package com.inforeis.vitalpet

import android.app.Application
import com.inforeis.vitalpet.services.LanguageManager
import com.inforeis.vitalpet.services.PetRepository

class VitalPetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LanguageManager.initialize(this)
        com.inforeis.vitalpet.services.ThemeManager.initialize(this)
        PetRepository.initialize(this)
        com.inforeis.vitalpet.services.SubscriptionManager.initialize(this)
        com.inforeis.vitalpet.services.ClinicalNotificationManager.initNotificationChannels(this)
    }
}
