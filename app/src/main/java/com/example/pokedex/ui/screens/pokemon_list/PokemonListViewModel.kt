package com.example.pokedex.ui.screens.pokemon_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.domain.model.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonListUiState>(PokemonListUiState.Loading)
    val uiState: StateFlow<PokemonListUiState> = _uiState.asStateFlow()

    private var isInitialLoadDone = false

    init {
        loadInitialData()
        observePokemon()
    }

    private fun observePokemon() {
        viewModelScope.launch {
            repository.getAllPokemon().collect { pokemonList ->
                if (pokemonList.isNotEmpty()) {
                    _uiState.value = PokemonListUiState.Success(pokemonList)
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = PokemonListUiState.Loading
            try {
                // Carrega lista básica com tipos (sem stats)
                repository.loadBasicPokemonList()
                isInitialLoadDone = true
            } catch (e: Exception) {
                _uiState.value = PokemonListUiState.Error(e.message ?: "Erro ao carregar pokémons")
            }
        }
    }

    fun reload() {
        isInitialLoadDone = false
        loadInitialData()
    }

    suspend fun searchPokemon(query: String): List<com.example.pokedex.domain.model.Pokemon> {
        return repository.searchPokemon(query)
    }
}

sealed class PokemonListUiState {
    object Loading : PokemonListUiState()
    data class Success(val pokemonList: List<Pokemon>) : PokemonListUiState()
    data class Error(val message: String) : PokemonListUiState()
}