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

    private var currentOffset = 0
    private val pageSize = 10009

    init {
        loadPokemon()
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

    fun loadPokemon() {
        viewModelScope.launch {
            _uiState.value = PokemonListUiState.Loading
            try {
                repository.loadPokemonList(offset = currentOffset, limit = pageSize)
                currentOffset += pageSize
            } catch (e: Exception) {
                _uiState.value = PokemonListUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            try {
                repository.loadPokemonList(offset = currentOffset, limit = pageSize)
                currentOffset += pageSize
            } catch (e: Exception) {
                // Silenciosamente falha se não conseguir carregar mais
            }
        }
    }
}

sealed class PokemonListUiState {
    object Loading : PokemonListUiState()
    data class Success(val pokemonList: List<Pokemon>) : PokemonListUiState()
    data class Error(val message: String) : PokemonListUiState()
}