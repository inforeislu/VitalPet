package com.inforeis.vitalpet.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.AccentRose
import com.inforeis.vitalpet.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BCSEvaluatorScreen(onBack: () -> Unit) {
    var bcsScore by remember { mutableFloatStateOf(5f) }
    val score = bcsScore.toInt()

    val (category, description, color) = when (score) {
        1 -> Triple("Caquético / Muito Magro".localized(), "Costelas, vértebras lombares e ossos da bacia facilmente visíveis. Nenhuma gordura palpável. Perda evidente de massa muscular.", AccentRose)
        2 -> Triple("Muito Abaixo do Peso".localized(), "Costelas facilmente palpáveis com mínima cobertura de gordura. Vértebras lombares evidentes. Cintura vincada acentuada.", AccentAmber)
        3 -> Triple("Abaixo do Peso Ideal".localized(), "Costelas palpáveis facilmente sem gordura em excesso. Cintura visível quando vista de cima. Concavidade abdominal evidente.", AccentAmber)
        4 -> Triple("Peso Ideal (Mínimo)".localized(), "Costelas palpáveis com leve cobertura de gordura. Cintura visível atrás das costelas. Abdómen bem delineado.", TealPrimary)
        5 -> Triple("Peso Ideal (Perfeito)".localized(), "Padrão ouro WSAVA: costelas facilmente palpáveis sem excesso de gordura. Cintura evidente e dobra abdominal visível.", TealPrimary)
        6 -> Triple("Ligeiro Sobrepeso".localized(), "Costelas palpáveis com leve excesso de gordura de cobertura. Cintura ainda discernível mas pouco evidente.", AccentAmber)
        7 -> Triple("Acima do Peso".localized(), "Costelas difíceis de palpar devido a capa espessa de gordura. Depósitos adiposos na zona lombar e na base da cauda.", AccentAmber)
        8 -> Triple("Obeso".localized(), "Costelas muito difíceis de palpar ou inacessíveis. Distensão abdominal evidente e perda total de linha de cintura.", AccentRose)
        else -> Triple("Obesidade Severa".localized(), "Depósitos maciços de gordura no tórax, coluna e base da cauda. Abdómen pendular e mobilidade comprometida.", AccentRose)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Avaliador BCS (1-9)".localized(), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar".localized())
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Arraste a barra para selecionar a condição corporal do pet:".localized(),
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Score: $score/9", fontWeight = FontWeight.Bold, color = color)
                Text(category, fontWeight = FontWeight.Bold, color = color)
            }

            Slider(
                value = bcsScore,
                onValueChange = { bcsScore = it },
                valueRange = 1f..9f,
                steps = 7
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.15f)) {
                        Text(
                            text = category,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Critérios Clínicos WSAVA:".localized(),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
