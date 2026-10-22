package com.inforeis.vitalpet.services

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.inforeis.vitalpet.MainActivity
import com.inforeis.vitalpet.R
import com.inforeis.vitalpet.models.Pet
import java.util.Locale

class PetQuickWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        const val EXTRA_ROUTE = "extra_route"
        const val ROUTE_SOS_CARD = "digital_id"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val pets = PetRepository.pets.value
            val pet: Pet? = pets.firstOrNull()

            val views = RemoteViews(context.packageName, R.layout.widget_pet_quick_status)

            if (pet != null) {
                views.setTextViewText(R.id.widget_pet_name, pet.name)
                views.setTextViewText(R.id.widget_pet_breed, "${pet.species.displayName} • ${pet.breed}")
                views.setTextViewText(R.id.widget_weight_val, String.format(Locale.US, "%.1f kg", pet.currentWeightKg))
                views.setTextViewText(R.id.widget_calories_val, String.format(Locale.US, "%.0f kcal", pet.der))

                val activeFasting = pet.fastingLogs.firstOrNull { !it.isCompleted }
                if (activeFasting != null) {
                    views.setTextViewText(R.id.widget_status_pill, "Jejum Ativo")
                    views.setTextColor(R.id.widget_status_pill, 0xFFF59E0B.toInt())
                    views.setTextViewText(R.id.widget_fasting_info, "Jejum: ${activeFasting.procedureDisplayName}")
                } else {
                    views.setTextViewText(R.id.widget_status_pill, "Saúde OK")
                    views.setTextColor(R.id.widget_status_pill, 0xFF14B8A6.toInt())
                    views.setTextViewText(R.id.widget_fasting_info, "BCS: ${pet.bcs}/9 • ${pet.bcsCategory}")
                }
            } else {
                views.setTextViewText(R.id.widget_pet_name, "VitalPet")
                views.setTextViewText(R.id.widget_pet_breed, "Gestão Clínica Veterinária")
                views.setTextViewText(R.id.widget_weight_val, "-- kg")
                views.setTextViewText(R.id.widget_calories_val, "-- kcal")
                views.setTextViewText(R.id.widget_status_pill, "Sem Pet")
                views.setTextViewText(R.id.widget_fasting_info, "Toque para adicionar um animal")
            }

            // Intent to open Main App
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openAppPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, openAppPendingIntent)

            // Intent for SOS Button
            val sosIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_ROUTE, ROUTE_SOS_CARD)
            }
            val sosPendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId + 1000,
                sosIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_sos, sosPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun notifyDataChanged(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, PetQuickWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                val intent = Intent(context, PetQuickWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }
}
