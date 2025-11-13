package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pokedex.R
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokedex.ui.screens.pokemon_detail.PokemonDetailScreen
import com.example.pokedex.ui.screens.team_builder.TeamBuilderScreen
import com.example.pokedex.ui.screens.pokemon_list.PokemonListScreen
import com.example.pokedex.ui.theme.PokedexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                val navController = rememberNavController()
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry.value?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (shouldShowBottomBar(currentDestination)) {
                            BottomNavigationBar(
                                selectedRoute = currentDestination?.route,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = NavRoute.Pokedex.route,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        composable(NavRoute.Pokedex.route) {
                            PokemonListScreen(
                                onPokemonClick = { pokemonId ->
                                    navController.navigate("${NavRoute.PokemonDetail.route}/$pokemonId")
                                }
                            )
                        }

                        composable(NavRoute.TeamBuilder.route) {
                            TeamBuilderScreen(
                                onNavigateToPokedex = {
                                    navController.navigate(NavRoute.Pokedex.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }

                        composable(
                            route = "${NavRoute.PokemonDetail.route}/{pokemonId}",
                            arguments = listOf(
                                navArgument("pokemonId") {
                                    type = NavType.IntType
                                }
                            )
                        ) { backStackEntry ->
                            val pokemonId = backStackEntry.arguments?.getInt("pokemonId") ?: 1
                            PokemonDetailScreen(
                                pokemonId = pokemonId,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun shouldShowBottomBar(destination: NavDestination?): Boolean {
        val route = destination?.route ?: return true
        return when {
            route.startsWith(NavRoute.PokemonDetail.route) -> false
            else -> true
        }
    }
}

private enum class NavRoute(
    val route: String,
    val label: String,
    val iconRes: Int
) {
    Pokedex(
        route = "pokedex",
        label = "Pokédex",
        iconRes = R.drawable.ic_pokedex
    ),
    TeamBuilder(
        route = "team_builder",
        label = "Equipes",
        iconRes = R.drawable.ic_team_builder
    ),
    PokemonDetail(
        route = "pokemon_detail",
        label = "detalhes",
        iconRes = android.R.drawable.ic_menu_info_details
    );
}

@Composable
private fun BottomNavigationBar(
    selectedRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar (
        containerColor = Color.White
    ) {
        listOf(NavRoute.Pokedex, NavRoute.TeamBuilder).forEach { item ->
            val selected = selectedRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },

                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.width(25.dp).height(25.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFF0A1AE),
                    indicatorColor = Color(0xFFDC0A2D)
                )
            )
        }
    }
}