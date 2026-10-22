package com.inforeis.vitalpet.services

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.inforeis.vitalpet.MainActivity
import com.inforeis.vitalpet.R
import com.inforeis.vitalpet.models.FastingLog
import com.inforeis.vitalpet.services.LanguageManager.localized
import java.util.Date

object ClinicalNotificationManager {

    const val CHANNEL_FASTING = "vitalpet_channel_fasting"
    const val CHANNEL_MEDICATION = "vitalpet_channel_medication"
    const val CHANNEL_WEIGH_IN = "vitalpet_channel_weigh_in"
    const val CHANNEL_GENERAL = "vitalpet_channel_general"

    private const val PREFS_NAME = "vitalpet_notification_prefs"
    private const val KEY_FASTING_NOTIFICATIONS_ENABLED = "fasting_notifications_enabled"
    private const val KEY_WEIGH_IN_NOTIFICATIONS_ENABLED = "weigh_in_notifications_enabled"

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            val fastingChannel = NotificationChannel(
                CHANNEL_FASTING,
                "Jejum Cirúrgico e Pré-Operatório".localized(),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas de corte de sólidos, água e prontidão para cirurgia.".localized()
                enableVibration(true)
            }

            val medChannel = NotificationChannel(
                CHANNEL_MEDICATION,
                "Lembretes de Medicação".localized(),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Horários de administração de fármacos e tratamentos.".localized()
                enableVibration(true)
            }

            val weighInChannel = NotificationChannel(
                CHANNEL_WEIGH_IN,
                "Acompanhamento de Peso".localized(),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Lembretes periódicos de pesagem e condição corporal.".localized()
            }

            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "Alertas Gerais Clínicos".localized(),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos de vacinação, desparasitação e consultas.".localized()
            }

            manager.createNotificationChannels(
                listOf(fastingChannel, medChannel, weighInChannel, generalChannel)
            )
        }
    }

    fun isFastingNotificationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_FASTING_NOTIFICATIONS_ENABLED, true)
    }

    fun setFastingNotificationEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_FASTING_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun isWeighInNotificationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_WEIGH_IN_NOTIFICATIONS_ENABLED, true)
    }

    fun setWeighInNotificationEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_WEIGH_IN_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun scheduleFastingAlerts(context: Context, petName: String, fastingLog: FastingLog) {
        if (!isFastingNotificationEnabled(context)) return

        val now = System.currentTimeMillis()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // 1. Food Cutoff Alert
        val foodCutoffMillis = fastingLog.foodCutoffDate.time
        if (foodCutoffMillis > now) {
            val title = "VitalPet: Início do Jejum de Sólidos".localized()
            val message = "Retire toda a alimentação sólida de %s agora. Apenas água fresca é permitida.".localized().format(petName)
            scheduleExactAlarm(
                context = context,
                alarmManager = alarmManager,
                triggerAtMillis = foodCutoffMillis,
                requestCode = (fastingLog.id.hashCode() + 1),
                title = title,
                message = message,
                channelId = CHANNEL_FASTING
            )
        }

        // 2. Water Cutoff Alert
        val waterCutoffMillis = fastingLog.waterCutoffDate.time
        if (waterCutoffMillis > now) {
            val title = "VitalPet: Jejum Total Obrigatório".localized()
            val message = "Retire a água de %s agora. O animal deve permanecer em jejum absoluto até ao procedimento.".localized().format(petName)
            scheduleExactAlarm(
                context = context,
                alarmManager = alarmManager,
                triggerAtMillis = waterCutoffMillis,
                requestCode = (fastingLog.id.hashCode() + 2),
                title = title,
                message = message,
                channelId = CHANNEL_FASTING
            )
        }

        // 3. Procedure Readiness Alert
        val procedureMillis = fastingLog.procedureDate.time
        if (procedureMillis > now) {
            val title = "VitalPet: Hora do Procedimento Clínico".localized()
            val message = "%s concluiu o protocolo de jejum com sucesso e está pronto(a) para %s.".localized().format(petName, fastingLog.procedureDisplayName)
            scheduleExactAlarm(
                context = context,
                alarmManager = alarmManager,
                triggerAtMillis = procedureMillis,
                requestCode = (fastingLog.id.hashCode() + 3),
                title = title,
                message = message,
                channelId = CHANNEL_FASTING
            )
        }
    }

    fun cancelFastingAlerts(context: Context, fastingId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val baseCode = fastingId.hashCode()

        for (offset in 1..3) {
            val intent = Intent(context, ClinicalAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                baseCode + offset,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
    }

    fun schedulePeriodicWeighInReminder(context: Context, petName: String, intervalDays: Int = 14) {
        if (!isWeighInNotificationEnabled(context)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerAt = System.currentTimeMillis() + (intervalDays * 24L * 60L * 60L * 1000L)

        val title = "VitalPet: Hora da Pesagem de Controlo".localized()
        val message = "Já passaram %d dias. Registe o peso atual de %s para acompanhar a curva nutricional.".localized().format(intervalDays, petName)

        scheduleExactAlarm(
            context = context,
            alarmManager = alarmManager,
            triggerAtMillis = triggerAt,
            requestCode = (petName.hashCode() + 999),
            title = title,
            message = message,
            channelId = CHANNEL_WEIGH_IN
        )
    }

    fun sendTestNotification(context: Context) {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1001,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_FASTING)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("VitalPet: Notificação de Teste".localized())
            .setContentText("O sistema de alertas clínicos e notificações está ativo e configurado com sucesso.".localized())
            .setStyle(NotificationCompat.BigTextStyle().bigText("O sistema de alertas clínicos e notificações está ativo e configurado com sucesso.".localized()))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 300, 200, 300))

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(1001, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun scheduleExactAlarm(
        context: Context,
        alarmManager: AlarmManager,
        triggerAtMillis: Long,
        requestCode: Int,
        title: String,
        message: String,
        channelId: String
    ) {
        val intent = Intent(context, ClinicalAlarmReceiver::class.java).apply {
            putExtra(ClinicalAlarmReceiver.EXTRA_TITLE, title)
            putExtra(ClinicalAlarmReceiver.EXTRA_MESSAGE, message)
            putExtra(ClinicalAlarmReceiver.EXTRA_CHANNEL_ID, channelId)
            putExtra(ClinicalAlarmReceiver.EXTRA_NOTIFICATION_ID, requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }
}
