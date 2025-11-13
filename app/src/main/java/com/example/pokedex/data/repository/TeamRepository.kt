package com.example.pokedex.data.repository

import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.dao.TeamDao
import com.example.pokedex.data.local.entities.TeamMemberEntity
import com.example.pokedex.domain.model.TeamMember
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TeamRepository @Inject constructor(
    private val teamDao: TeamDao,
    private val pokemonDao: PokemonDao
) {

    // Observa o time atual
    fun getTeam(): Flow<List<TeamMember>> {
        return teamDao.getTeam().map { entities ->
            entities.map { it.toTeamMember() }
        }
    }

    // Verifica se pode adicionar (máximo 6)
    suspend fun canAddToTeam(): Boolean {
        return teamDao.getTeamSize() < 6
    }

    // Verifica se o Pokémon já está no time
    suspend fun isPokemonInTeam(pokemonId: Int): Boolean {
        return teamDao.isPokemonInTeam(pokemonId)
    }

    // Adiciona Pokémon ao time
    suspend fun addToTeam(pokemonId: Int): Result<Unit> {
        return try {
            // Verifica limite
            if (!canAddToTeam()) {
                return Result.failure(Exception("Time completo! Máximo de 6 Pokémon."))
            }

            // Verifica se já está no time
            if (isPokemonInTeam(pokemonId)) {
                return Result.failure(Exception("Este Pokémon já está no seu time!"))
            }

            // Busca informações do Pokémon
            val pokemon = pokemonDao.getPokemonById(pokemonId)
                ?: return Result.failure(Exception("Pokémon não encontrado"))

            // Adiciona ao time
            val teamMember = TeamMemberEntity(
                pokemonId = pokemon.id,
                name = pokemon.name,
                imageUrl = pokemon.imageUrl,
                type1 = pokemon.type1,
                type2 = pokemon.type2
            )

            teamDao.addToTeam(teamMember)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Remove Pokémon do time
    suspend fun removeFromTeam(pokemonId: Int) {
        teamDao.removeFromTeam(pokemonId)
    }

    // Limpa todo o time
    suspend fun clearTeam() {
        teamDao.clearTeam()
    }

    // Converte Entity para Model
    private fun TeamMemberEntity.toTeamMember(): TeamMember {
        val types = mutableListOf(type1)
        type2?.let { types.add(it) }

        return TeamMember(
            pokemonId = pokemonId,
            name = name,
            imageUrl = imageUrl,
            types = types
        )
    }
}