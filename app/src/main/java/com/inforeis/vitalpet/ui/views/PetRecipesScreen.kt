package com.inforeis.vitalpet.ui.views

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.inforeis.vitalpet.services.LanguageManager.localized
import com.inforeis.vitalpet.ui.theme.AccentAmber
import com.inforeis.vitalpet.ui.theme.TealPrimary

data class RecipeItem(
    val title: String,
    val category: String,
    val indications: String,
    val ingredients: List<String>,
    val preparation: String,
    val isCustom: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetRecipesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val gson = remember { Gson() }
    val prefs = remember { context.getSharedPreferences("vitalpet_recipes", Context.MODE_PRIVATE) }

    var selectedRecipe by remember { mutableStateOf<RecipeItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val defaultRecipes = remember {
        listOf(
            RecipeItem(
                title = "Dieta Branda Gastrointestinal".localized(),
                category = "Recuperação Gástrica".localized(),
                indications = "Indicada para episódios de vómitos transitórios, diarreia leve ou pós-cirúrgico gastrointestinal.".localized(),
                ingredients = listOf(
                    "60% Peito de frango cozido sem pele, sem ossos e sem sal".localized(),
                    "40% Arroz branco cozido bem empapado (amido digestível)".localized(),
                    "Opcional: 1 colher de puré de abóbora cozida (fonte de fibra solúvel)".localized()
                ),
                preparation = "Coza o peito de frango e desfie finamente. Coza o arroz em abundância de água até ficar macio. Misture sem adicionar sal, alho, cebola ou gordura. Sirva morno em pequenas porções ao longo do dia.".localized()
            ),
            RecipeItem(
                title = "Caldo de Ossos Suave (Articular & Renal)".localized(),
                category = "Hidratação & Suporte Articular".localized(),
                indications = "Rico em colagénio, glicosamina e minerais essenciais. Excelente para hidratação e estímulo de apetite.".localized(),
                ingredients = listOf(
                    "1 kg de ossos carnudos de vaca ou frango".localized(),
                    "2 litros de água filtrada".localized(),
                    "1 colher de sopa de vinagre de sidra (para extrair minerais)".localized()
                ),
                preparation = "Coloque os ossos, água e vinagre numa panela. Cozinhe em lume muito brando durante 6 a 12 horas. Coe com um passador fino descartando TODOS os ossos. Deixe arrefecer no frigorífico e remova a camada de gordura superficial antes de servir.".localized()
            ),
            RecipeItem(
                title = "Refeição Cozida Completa (Carne & Legumes)".localized(),
                category = "Alimentação Natural Cozida".localized(),
                indications = "Alternativa nutritiva a ração industrial sob orientação nutrológica veterinária.".localized(),
                ingredients = listOf(
                    "50% Carne magra (peru, frango ou vaca magra cortada em cubos)".localized(),
                    "20% Fígado e coração bovino cozidos".localized(),
                    "20% Vegetais seguros (cenoura, abobrinha, feijão-verde cozidos)".localized(),
                    "10% Batata-doce ou aveia integral cozida".localized()
                ),
                preparation = "Cozinhe a carne e os vegetais a vapor ou com pouca água para preservar nutrientes. Pique e misture tudo. Adicione um suplemento vitamínico-mineral prescrito pelo médico veterinário.".localized()
            ),
            RecipeItem(
                title = "Snacks Naturais de Cenoura & Aveia".localized(),
                category = "Petiscos Saudáveis".localized(),
                indications = "Petisco crocante de baixo teor calórico para treino e recompensas saudáveis.".localized(),
                ingredients = listOf(
                    "1 chávena de farinha de aveia integral".localized(),
                    "1 cenoura média ralada finamente".localized(),
                    "1 ovo inteiro batido".localized(),
                    "2 colheres de água se necessário".localized()
                ),
                preparation = "Misture os ingredientes até obter uma massa moldável. Estenda e corte em pequenos formatos. Asse a 180°C durante 15 a 20 minutos até ficarem dourados e crocantes. Conserve em frasco hermético.".localized()
            )
        )
    }

    val customRecipes = remember {
        mutableStateListOf<RecipeItem>().apply {
            val json = prefs.getString("custom_recipes", null)
            if (!json.isNullOrEmpty()) {
                try {
                    val type = object : TypeToken<List<RecipeItem>>() {}.type
                    val list: List<RecipeItem> = gson.fromJson(json, type)
                    addAll(list)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    val allRecipes = defaultRecipes + customRecipes

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Receitas Culinárias".localized(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .padding(start = 14.dp)
                            .size(40.dp)
                            .clickable { onBack() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar".localized(),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TealPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Receita".localized())
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AccentAmber.copy(alpha = 0.15f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Restaurant, contentDescription = null, tint = AccentAmber)
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Nutrição & Culinária Veterinária".localized(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Receitas seguras desenvolvidas para dietas terapêuticas e suporte digestivo.".localized(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                            )
                        }
                    }
                }
            }

            items(allRecipes) { recipe ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedRecipe = if (selectedRecipe == recipe) null else recipe
                        }
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = recipe.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    if (recipe.isCustom) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(shape = RoundedCornerShape(4.dp), color = TealPrimary.copy(alpha = 0.15f)) {
                                            Text("Própria".localized(), color = TealPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = recipe.category,
                                    style = MaterialTheme.typography.labelMedium.copy(color = TealPrimary, fontWeight = FontWeight.SemiBold)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                            )
                        }

                        if (selectedRecipe == recipe) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Indicações:".localized(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            )
                            Text(
                                text = recipe.indications,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Ingredientes:".localized(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            )
                            recipe.ingredients.forEach { ing ->
                                Text(
                                    text = "• $ing",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Modo de Preparo:".localized(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            )
                            Text(
                                text = recipe.preparation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("") }
        var newIndications by remember { mutableStateOf("") }
        var newIngredients by remember { mutableStateOf("") }
        var newPrep by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Nova Receita Personalizada".localized(), fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Título da Receita".localized()) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        label = { Text("Categoria (ex: Dieta Cozida)".localized()) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newIndications,
                        onValueChange = { newIndications = it },
                        label = { Text("Indicações Clínicas".localized()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newIngredients,
                        onValueChange = { newIngredients = it },
                        label = { Text("Ingredientes (1 por linha)".localized()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPrep,
                        onValueChange = { newPrep = it },
                        label = { Text("Instruções de Preparo".localized()) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            val ingredientsList = newIngredients.split("\n").filter { it.isNotBlank() }
                            val newRecipe = RecipeItem(
                                title = newTitle,
                                category = newCategory.ifBlank { "Receita Caseira".localized() },
                                indications = newIndications.ifBlank { "Receita personalizada registada pelo tutor.".localized() },
                                ingredients = ingredientsList.ifEmpty { listOf(newIngredients) },
                                preparation = newPrep.ifBlank { "Sem instruções específicas.".localized() },
                                isCustom = true
                            )
                            customRecipes.add(newRecipe)
                            prefs.edit().putString("custom_recipes", gson.toJson(customRecipes.toList())).apply()
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Guardar".localized())
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar".localized())
                }
            }
        )
    }
}
