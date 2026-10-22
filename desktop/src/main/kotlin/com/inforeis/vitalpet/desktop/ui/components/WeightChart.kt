package com.inforeis.vitalpet.desktop.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inforeis.vitalpet.desktop.ui.theme.AmberAccent
import com.inforeis.vitalpet.desktop.ui.theme.TealPrimary
import com.inforeis.vitalpet.models.WeightLog
import com.inforeis.vitalpet.services.LanguageManager.localized
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DesktopWeightEvolutionChart(
    weightLogs: List<WeightLog>,
    targetWeightKg: Double?,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(220.dp)
) {
    if (weightLogs.size < 2) {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Registe pelo menos 2 pesagens para visualizar a curva de evolução ponderal.".localized(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val sortedLogs = weightLogs.sortedBy { it.date }
    val weights = sortedLogs.map { it.weightKg }
    val minW = maxOf(0.0, (weights.minOrNull() ?: 0.0) - 1.0)
    val maxW = (weights.maxOrNull() ?: 10.0) + 1.0
    val weightRange = maxOf(1.0, maxW - minW)

    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    Canvas(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        val width = size.width
        val height = size.height - 30f // espaço para rótulos de data
        val pointSpacing = width / (sortedLogs.size - 1)

        val points = sortedLogs.mapIndexed { index, log ->
            val x = index * pointSpacing
            val yRatio = ((log.weightKg - minW) / weightRange).toFloat()
            val y = height - (yRatio * height)
            Offset(x, y)
        }

        // 1. Linha de Peso Alvo (Se definido)
        if (targetWeightKg != null && targetWeightKg in minW..maxW) {
            val targetYRatio = ((targetWeightKg - minW) / weightRange).toFloat()
            val targetY = height - (targetYRatio * height)
            drawLine(
                color = AmberAccent,
                start = Offset(0f, targetY),
                end = Offset(width, targetY),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            )
        }

        // 2. Preenchimento de Gradiente sob a Curva
        val fillPath = Path().apply {
            moveTo(points.first().x, height)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(TealPrimary.copy(alpha = 0.35f), TealPrimary.copy(alpha = 0.02f)),
                startY = 0f,
                endY = height
            )
        )

        // 3. Linha da Curva Ponderal
        val strokePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        drawPath(
            path = strokePath,
            color = TealPrimary,
            style = Stroke(width = 3.5f)
        )

        // 4. Pontos e Valores
        points.forEachIndexed { index, point ->
            drawCircle(
                color = Color.White,
                radius = 6f,
                center = point
            )
            drawCircle(
                color = TealPrimary,
                radius = 4f,
                center = point
            )

            // Rótulo da data
            val dateStr = dateFormat.format(sortedLogs[index].date)
            drawContext.canvas.nativeCanvas.drawString(
                dateStr,
                point.x - 14f,
                size.height - 4f,
                org.jetbrains.skia.Font(null, 10f),
                org.jetbrains.skia.Paint().apply {
                    color = org.jetbrains.skia.Color.makeRGB(140, 150, 165)
                }
            )

            // Rótulo de peso
            val weightStr = "${sortedLogs[index].weightKg}kg"
            drawContext.canvas.nativeCanvas.drawString(
                weightStr,
                point.x - 16f,
                point.y - 10f,
                org.jetbrains.skia.Font(null, 10f),
                org.jetbrains.skia.Paint().apply {
                    color = org.jetbrains.skia.Color.makeRGB(13, 148, 136)
                }
            )
        }
    }
}
