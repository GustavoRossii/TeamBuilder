package com.example.pokedex.ui.screens.pokemon_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.data.repository.TeamRepository
import com.example.pokedex.domain.model.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val teamRepository: TeamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState.Loading)
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun loadPokemon(pokemonId: Int) {
        viewModelScope.launch {
            _uiState.value = PokemonDetailUiState.Loading
            try {
                val pokemon = repository.getPokemonById(pokemonId)
                if (pokemon != null) {
                    _uiState.value = PokemonDetailUiState.Success(pokemon)
                } else {
                    _uiState.value = PokemonDetailUiState.Error("Pokémon não encontrado")
                }
            } catch (e: Exception) {
                _uiState.value = PokemonDetailUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun addToTeam(pokemonId: Int) {
        viewModelScope.launch {
            teamRepository.addToTeam(pokemonId).fold(
                onSuccess = {
                    _message.value = "Pokémon adicionado ao time!"
                },
                onFailure = { e ->
                    _message.value = e.message ?: "Erro ao adicionar Pokémon ao time"
                }
            )
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}

sealed class PokemonDetailUiState {
    object Loading : PokemonDetailUiState()
    data class Success(val pokemon: Pokemon) : PokemonDetailUiState()
    data class Error(val message: String) : PokemonDetailUiState()
}