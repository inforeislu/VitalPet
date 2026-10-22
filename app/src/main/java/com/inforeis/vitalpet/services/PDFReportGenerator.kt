package com.inforeis.vitalpet.services

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.inforeis.vitalpet.models.Pet
import com.inforeis.vitalpet.services.LanguageManager.localized
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PDFReportGenerator {

    fun generateReport(context: Context, pet: Pet, language: String = LanguageManager.currentLanguage.value): File? {
        return try {
            val pdfDoc = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Background Header Bar (Teal Primary: #0D9488)
            paint.color = Color.rgb(13, 148, 136)
            canvas.drawRect(0f, 0f, pageWidth.toFloat(), 90f, paint)

            // Header Title
            paint.color = Color.WHITE
            paint.textSize = 20f
            paint.isFakeBoldText = true
            canvas.drawText("Relatório Clínico & Nutricional".localized(language), 40f, 42f, paint)

            paint.textSize = 12f
            paint.isFakeBoldText = false
            paint.color = Color.rgb(204, 251, 241)
            val emissionDate = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            canvas.drawText("${"Data de Emissão: ".localized(language)}$emissionDate • VitalPet Health", 40f, 68f, paint)

            var currentY = 120f
            val labelColor = Color.rgb(30, 41, 59)
            val subColor = Color.rgb(100, 116, 139)
            val accentTeal = Color.rgb(13, 148, 136)
            val alertRed = Color.rgb(225, 29, 72)

            // Section: Identificação do Paciente
            paint.color = accentTeal
            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas.drawText("1. ${"Identificação do Paciente".localized(language)}", 40f, currentY, paint)
            currentY += 8f

            paint.color = Color.rgb(226, 232, 240)
            canvas.drawLine(40f, currentY, (pageWidth - 40).toFloat(), currentY, paint)
            currentY += 18f

            paint.color = labelColor
            paint.textSize = 11f
            paint.isFakeBoldText = false
            canvas.drawText("${"Nome: ".localized(language)}${pet.name}   |   ${"Espécie: ".localized(language)}${pet.species.displayName(language)}   |   ${"Sexo: ".localized(language)}${pet.gender.displayName(language)} (${pet.gender.symbol})", 40f, currentY, paint)
            currentY += 16f
            canvas.drawText("${"Raça: ".localized(language)}${pet.breed}   |   ${"Idade: ".localized(language)}${pet.ageInYears} ${"anos".localized(language)} (${dateFormat.format(pet.birthDate)})", 40f, currentY, paint)
            currentY += 16f
            canvas.drawText("${"Estado Reprodutivo: ".localized(language)}${pet.neuterStatus.displayName(language)}   |   ${"Nível de Atividade: ".localized(language)}${pet.activityLevel.displayName(language)}", 40f, currentY, paint)
            currentY += 16f
            if (!pet.microchipNumber.isNullOrEmpty()) {
                canvas.drawText("${"Microchip: ".localized(language)}${pet.microchipNumber}", 40f, currentY, paint)
                currentY += 16f
            }
            if (!pet.clinicContact.isNullOrEmpty()) {
                canvas.drawText("${"Contacto Clínico SOS: ".localized(language)}${pet.clinicContact}", 40f, currentY, paint)
                currentY += 16f
            }

            currentY += 12f

            // Section: Biometria & Necessidades Energéticas (WSAVA)
            paint.color = accentTeal
            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas.drawText("2. ${"Biometria & Metabolismo (WSAVA / NRC)".localized(language)}", 40f, currentY, paint)
            currentY += 8f
            paint.color = Color.rgb(226, 232, 240)
            canvas.drawLine(40f, currentY, (pageWidth - 40).toFloat(), currentY, paint)
            currentY += 18f

            paint.color = labelColor
            paint.textSize = 11f
            paint.isFakeBoldText = false
            val weightStr = "%.2f kg".format(pet.currentWeightKg)
            val targetStr = pet.targetWeightKg?.let { " (Alvo: %.2f kg)".format(it) } ?: ""
            canvas.drawText("• ${"Peso Atual: ".localized(language)}$weightStr$targetStr", 40f, currentY, paint)
            currentY += 16f
            canvas.drawText("• ${"Score Corporal (BCS): ".localized(language)}${pet.bcs}/9 (${pet.bcsCategory})", 40f, currentY, paint)
            currentY += 16f
            canvas.drawText("• ${"RER (Taxa Metabólica Basal): ".localized(language)}%.0f kcal/dia".format(pet.rer), 40f, currentY, paint)
            currentY += 16f
            canvas.drawText("• ${"DER (Necessidade Calórica Diária): ".localized(language)}%.0f kcal/dia".format(pet.der), 40f, currentY, paint)
            currentY += 24f

            // Section: Plano Alimentar Ativo
            paint.color = accentTeal
            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas.drawText("3. ${"Plano Nutricional Prescrito".localized(language)}", 40f, currentY, paint)
            currentY += 8f
            paint.color = Color.rgb(226, 232, 240)
            canvas.drawLine(40f, currentY, (pageWidth - 40).toFloat(), currentY, paint)
            currentY += 18f

            paint.color = labelColor
            paint.textSize = 11f
            paint.isFakeBoldText = false
            if (pet.dietPlans.isEmpty()) {
                paint.color = subColor
                canvas.drawText("• ${"Sem planos nutricionais ativos.".localized(language)}", 40f, currentY, paint)
                currentY += 16f
            } else {
                for (plan in pet.dietPlans) {
                    canvas.drawText("• ${plan.foodName}: %.0f g/dia (%.0f kcal/100g, Alvo: %.0f kcal/dia)".format(plan.dailyPortionGrams, plan.kcalPer100g, plan.targetDailyKcal), 40f, currentY, paint)
                    currentY += 16f
                }
            }
            currentY += 12f

            // Section: Vacinação & Saúde Clínica
            paint.color = accentTeal
            paint.textSize = 14f
            paint.isFakeBoldText = true
            canvas.drawText("4. ${"Vacinação & Profilaxia".localized(language)}", 40f, currentY, paint)
            currentY += 8f
            paint.color = Color.rgb(226, 232, 240)
            canvas.drawLine(40f, currentY, (pageWidth - 40).toFloat(), currentY, paint)
            currentY += 18f

            paint.color = labelColor
            paint.textSize = 11f
            paint.isFakeBoldText = false
            if (pet.vaccineLogs.isEmpty()) {
                paint.color = subColor
                canvas.drawText("• ${"Sem vacinas registadas.".localized(language)}", 40f, currentY, paint)
                currentY += 16f
            } else {
                for (v in pet.vaccineLogs.prefixOrAll(4)) {
                    val nextStr = v.nextDueDate?.let { " | ${"Próxima: ".localized(language)}${dateFormat.format(it)}" } ?: ""
                    canvas.drawText("• ${v.vaccineName} (${dateFormat.format(v.applicationDate)})$nextStr", 40f, currentY, paint)
                    currentY += 16f
                }
            }

            if (pet.dewormingLogs.isNotEmpty()) {
                currentY += 6f
                for (d in pet.dewormingLogs.prefixOrAll(3)) {
                    val nextStr = d.nextDueDate?.let { " | ${"Próxima: ".localized(language)}${dateFormat.format(it)}" } ?: ""
                    canvas.drawText("• ${d.productName} [${d.type.displayName(language)}] (${dateFormat.format(d.applicationDate)})$nextStr", 40f, currentY, paint)
                    currentY += 16f
                }
            }
            currentY += 12f

            // Section: Alergias e Cuidados Críticos
            if (pet.allergies.isNotEmpty() || pet.chronicConditions.isNotEmpty()) {
                paint.color = alertRed
                paint.textSize = 13f
                paint.isFakeBoldText = true
                canvas.drawText("⚠️ ${"Alertas Médicos & Alergias".localized(language)}", 40f, currentY, paint)
                currentY += 16f

                paint.textSize = 11f
                paint.isFakeBoldText = false
                if (pet.allergies.isNotEmpty()) {
                    canvas.drawText("• ${"Alergias Identificadas: ".localized(language)}${pet.allergies.joinToString(", ")}", 40f, currentY, paint)
                    currentY += 16f
                }
                if (pet.chronicConditions.isNotEmpty()) {
                    canvas.drawText("• ${"Condições Crónicas: ".localized(language)}${pet.chronicConditions.joinToString(", ")}", 40f, currentY, paint)
                    currentY += 16f
                }
                currentY += 10f
            }

            // Draw QR Code on Bottom Right
            val qrPayload = "VITALPET SOS | ${pet.name} | ${pet.species.displayName(language)} | Chip: ${pet.microchipNumber ?: "N/A"} | SOS: ${pet.clinicContact ?: "N/A"}"
            val qrBitmap = QRCodeGenerator.generateQRCode(qrPayload, 120)
            if (qrBitmap != null) {
                val qrRect = RectF((pageWidth - 160).toFloat(), (pageHeight - 160).toFloat(), (pageWidth - 40).toFloat(), (pageHeight - 40).toFloat())
                canvas.drawBitmap(qrBitmap, null, qrRect, null)
                paint.color = subColor
                paint.textSize = 9f
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("QR Code SOS", (pageWidth - 100).toFloat(), (pageHeight - 25).toFloat(), paint)
                paint.textAlign = Paint.Align.LEFT
            }

            // Footer note
            paint.color = subColor
            paint.textSize = 9f
            paint.isFakeBoldText = false
            canvas.drawText("Documento gerado pela aplicação VitalPet Android • Validação Clínica WSAVA/NRC", 40f, (pageHeight - 40).toFloat(), paint)

            pdfDoc.finishPage(page)

            // Save PDF to cache directory
            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()
            val sanitizedName = pet.name.replace("\\s+".toRegex(), "_")
            val pdfFile = File(reportsDir, "VitalPet_${sanitizedName}_Relatorio.pdf")

            FileOutputStream(pdfFile).use { out ->
                pdfDoc.writeTo(out)
            }
            pdfDoc.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareReport(context: Context, pet: Pet) {
        val pdfFile = generateReport(context, pet) ?: return
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Relatório Clínico - ${pet.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Partilhar Relatório Clínico (PDF)".localized()))
    }

    private fun <T> List<T>.prefixOrAll(count: Int): List<T> {
        return if (size <= count) this else subList(0, count)
    }
}
