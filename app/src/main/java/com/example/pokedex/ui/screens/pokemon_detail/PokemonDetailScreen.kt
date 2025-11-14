package com.example.pokedex.ui.screens.pokemon_detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.ui.screens.pokemon_list.getTypeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    onBackClick: () -> Unit,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val message by viewModel.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(pokemonId) {
        viewModel.loadPokemon(pokemonId)
    }

    // Snackbar para mensagens
    LaunchedEffect(message) {
        message?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is PokemonDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5))
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFFDC0A2D)
                        )
                    }
                }
                is PokemonDetailUiState.Success -> {
                    PokemonDetailContent(
                        pokemon = state.pokemon,
                        onBackClick = onBackClick,
                        onAddToTeam = { viewModel.addToTeam(state.pokemon.id) }
                    )
                }
                is PokemonDetailUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5))
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("😕", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(state.message, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailContent(
    pokemon: Pokemon,
    onBackClick: () -> Unit,
    onAddToTeam: () -> Unit
) {

    // Log de debug que pode ser removido depois
    android.util.Log.d("POKEMON_DEBUG", "Pokemon recebido pela UI: $pokemon")

    val primaryColor = getTypeColor(pokemon.types.firstOrNull() ?: "normal")
    val secondaryColor = primaryColor.copy(alpha = 0.6f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(primaryColor, secondaryColor, Color.White)
                )
            )
    ) {
        // Top Bar customizada
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "#${pokemon.id.toString().padStart(3, '0')}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nome do Pokémon
            Text(
                text = pokemon.name,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tipos
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pokemon.types.forEach { type ->
                    Surface(
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = type.replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Imagem do Pokémon
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .shadow(20.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = pokemon.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÃO ADICIONAR AO TIME
            Button(
                onClick = onAddToTeam,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = primaryColor
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "⭐",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        "Adicionar ao Time",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Card de informações
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Informações físicas
                    Text(
                        "Sobre",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                    ) {
                        // *** ALTERAÇÃO AQUI ***
                        InfoCard(
                            modifier = Modifier.weight(1f),
                            // icon = "⚖️", // <-- Removido
                            label = "Peso",
                            value = "${pokemon.weight / 10.0} kg"
                        )
                        // *** ALTERAÇÃO AQUI ***
                        InfoCard(
                            modifier = Modifier.weight(1f),
                            // icon = "📏", // <-- Removido
                            label = "Altura",
                            value = "${pokemon.height / 10.0} m"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Estatísticas
                    Text(
                        "Estatísticas Base",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedStatBar("HP", pokemon.hp, Color(0xFFFF5959))
                    AnimatedStatBar("Ataque", pokemon.attack, Color(0xFFF08030))
                    AnimatedStatBar("Ataque Especial", pokemon.specialAttack, Color(0xFF9B59B6))
                    AnimatedStatBar("Defesa Especial", pokemon.specialDefense, Color(0xFF3498DB))
                    AnimatedStatBar("Defesa", pokemon.defense, Color(0xFFF8D030))
                    AnimatedStatBar("Velocidade", pokemon.speed, Color(0xFF78C850))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// *** ALTERAÇÃO AQUI ***
@Composable
fun InfoCard(modifier: Modifier = Modifier, label: String, value: String) { // <-- Parâmetro "icon" removido
    Card(
        modifier = modifier.height(90.dp), // A altura foi mantida, mas agora caberá
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centraliza os dois textos
        ) {
            // *** O Text(icon, ...) FOI REMOVIDO DAQUI ***

            Text(
                label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            // Adicionado um pequeno espaço
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )
        }
    }
}

@Composable
fun AnimatedStatBar(label: String, value: Int, color: Color) {
    var animationPlayed by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animationPlayed) value / 255f else 0f,
        animationSpec = tween(1000),
        label = "stat_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(80.dp),
                color = Color.DarkGray
            )

            Text(
                value.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}