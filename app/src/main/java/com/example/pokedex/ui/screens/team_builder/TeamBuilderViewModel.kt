package com.example.pokedex.ui.screens.team_builder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.repository.TeamRepository
import com.example.pokedex.domain.model.TeamMember
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamBuilderViewModel @Inject constructor(
    private val repository: TeamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeamBuilderUiState>(TeamBuilderUiState.Loading)
    val uiState: StateFlow<TeamBuilderUiState> = _uiState.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        observeTeam()
    }

    private fun observeTeam() {
        viewModelScope.launch {
            repository.getTeam().collect { team ->
                _uiState.value = TeamBuilderUiState.Success(team)
            }
        }
    }

    fun removeFromTeam(pokemonId: Int) {
        viewModelScope.launch {
            try {
                repository.removeFromTeam(pokemonId)
                _message.value = "Pokémon removido do time"
            } catch (e: Exception) {
                _message.value = "Erro ao remover Pokémon: ${e.message}"
            }
        }
    }

    fun clearTeam() {
        viewModelScope.launch {
            try {
                repository.clearTeam()
                _message.value = "Time limpo com sucesso"
            } catch (e: Exception) {
                _message.value = "Erro ao limpar time: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}

sealed class TeamBuilderUiState {
    object Loading : TeamBuilderUiState()
    data class Success(val team: List<TeamMember>) : TeamBuilderUiState()
    data class Error(val message: String) : TeamBuilderUiState()
}

