package com.example.pokedex.data.repository

import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.entities.PokemonEntity
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val api: PokeApiService,
    private val dao: PokemonDao
) {

    // Observa os Pokémon do banco local
    fun getAllPokemon(): Flow<List<Pokemon>> {
        return dao.getAllPokemon().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    // Busca Pokémon por ID (primeiro tenta cache, depois API)
    suspend fun getPokemonById(id: Int): Pokemon? {
        // Tenta buscar do cache primeiro
        val cached = dao.getPokemonById(id)
        if (cached != null) {
            return cached.toDomainModel()
        }

        // Se não tem no cache, busca da API
        return try {
            val dto = api.getPokemonDetail(id)
            val entity = dto.toEntity()
            dao.insertPokemon(entity)
            entity.toDomainModel()
        } catch (e: Exception) {
            null
        }
    }

    // Carrega lista de Pokémon da API e salva no cache
    suspend fun loadPokemonList(offset: Int = 0, limit: Int = 20) {
        try {
            val response = api.getPokemonList(offset, limit)

            // Para cada Pokémon na lista, busca os detalhes
            val pokemonList = response.results.mapNotNull { item ->
                try {
                    // Extrai o ID da URL
                    val id = item.url.trimEnd('/').split("/").last().toInt()
                    val detail = api.getPokemonDetail(id)
                    detail.toEntity()
                } catch (e: Exception) {
                    null
                }
            }

            // Salva todos no banco
            dao.insertAllPokemon(pokemonList)
        } catch (e: Exception) {
            // Erro ao carregar da API, usa o cache existente
        }
    }

    // Mapeia DTO para Entity
    private fun com.example.pokedex.data.remote.dto.PokemonDetailDto.toEntity(): PokemonEntity {
        val types = this.types.sortedBy { it.slot }.map { it.type.name }

        return PokemonEntity(
            id = this.id,
            name = this.name.replaceFirstChar { it.uppercase() },
            imageUrl = this.sprites.front_default ?: "",
            type1 = types.getOrNull(0) ?: "unknown",
            type2 = types.getOrNull(1),
            height = this.height,
            weight = this.weight,
            hp = this.stats.find { it.stat.name == "hp" }?.base_stat ?: 0,
            attack = this.stats.find { it.stat.name == "attack" }?.base_stat ?: 0,
            specialAttack = this.stats.find { it.stat.name == "special-attack" }?.base_stat ?: 0,
            specialDefense = this.stats.find { it.stat.name == "special-defense" }?.base_stat ?: 0,
            defense = this.stats.find { it.stat.name == "defense" }?.base_stat ?: 0,
            speed = this.stats.find { it.stat.name == "speed" }?.base_stat ?: 0
        )
    }

    // Mapeia Entity para Domain Model
    private fun PokemonEntity.toDomainModel(): Pokemon {
        val types = mutableListOf(type1)
        type2?.let { types.add(it) }

        return Pokemon(
            id = this.id,
            name = this.name,
            imageUrl = this.imageUrl,
            types = types,
            height = this.height,
            weight = this.weight,
            hp = this.hp,
            attack = this.attack,
            specialAttack = this.specialAttack,
            specialDefense = this.specialDefense,
            defense = this.defense,
            speed = this.speed
        )
    }
}