package com.example.pokedex.ui.screens.team_builder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pokedex.domain.model.TeamMember
import com.example.pokedex.ui.screens.pokemon_list.getTypeColor

@Composable
fun TeamBuilderScreen(
    viewModel: TeamBuilderViewModel = hiltViewModel(),
    onNavigateToPokedex: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val message by viewModel.message.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Snackbar para mensagens
    LaunchedEffect(message) {
        message?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            // Limpa a mensagem após o snackbar ser descartado
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFDC0A2D),
                                Color(0xFFB3051F)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                // HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Pokébola ícone
                        Box(
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "⚪",
                                fontSize = 28.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                "Team Builder",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                "Monte seus times de pokémon",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF3F3),
                            Color(0xFFF5F5F5)
                        )
                    )
                )
        ) {
            when (val state = uiState) {
                is TeamBuilderUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFDC0A2D))
                    }
                }
                is TeamBuilderUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Erro: ${state.message}",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is TeamBuilderUiState.Success -> {
                    TeamContent(
                        team = state.team,
                        onRemove = { viewModel.removeFromTeam(it) },
                        onClearTeam = { viewModel.clearTeam() },
                        onNavigateToPokedex = onNavigateToPokedex
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamContent(
    team: List<TeamMember>,
    onRemove: (Int) -> Unit,
    onClearTeam: () -> Unit,
    onNavigateToPokedex: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Contador de Pokémon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Seu Time (${team.size}/6)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C)
            )
            if (team.isNotEmpty()) {
                TextButton(onClick = onClearTeam) {
                    Text(
                        text = "Limpar Time",
                        color = Color(0xFFDC0A2D),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cria lista de 6 slots (preenche com null se vazio)
        val slots = remember(team) {
            (0 until 6).map { index ->
                team.getOrNull(index)
            }
        }

        // Grid de 2 colunas para os 6 slots
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(slots.size) { index ->
                val teamMember = slots[index]
                if (teamMember != null) {
                    TeamMemberCard(
                        teamMember = teamMember,
                        onRemove = { onRemove(teamMember.pokemonId) }
                    )
                } else {
                    EmptyTeamSlot(
                        onClick = { onNavigateToPokedex?.invoke() }
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamMemberCard(
    teamMember: TeamMember,
    onRemove: () -> Unit
) {
    val primaryColor = getTypeColor(teamMember.types.firstOrNull() ?: "normal")
    val secondaryColor = primaryColor.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .shadow(8.dp, RoundedCornerShape(20.dp))
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(primaryColor, secondaryColor)
                        )
                    )
            ) {
                // Círculo decorativo de fundo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 15.dp, y = -15.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                )

                // Botão de remover
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remover",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Número
                    Text(
                        text = "#${teamMember.pokemonId.toString().padStart(3, '0')}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Nome
                    Text(
                        text = teamMember.name.replaceFirstChar { it.uppercaseChar() },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tipos
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        teamMember.types.take(2).forEach { type ->
                            Surface(
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = type.replaceFirstChar { it.uppercaseChar() },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Imagem do Pokémon
                AsyncImage(
                    model = teamMember.imageUrl,
                    contentDescription = teamMember.name,
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = -4.dp, y = -4.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun EmptyTeamSlot(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .shadow(4.dp, RoundedCornerShape(20.dp))
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.6f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "+",
                        fontSize = 48.sp,
                        color = Color(0xFFDC0A2D).copy(alpha = 0.5f),
                        fontWeight = FontWeight.Light
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Adicionar",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

