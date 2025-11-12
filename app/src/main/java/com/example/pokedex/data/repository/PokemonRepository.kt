package com.example.pokedex.data.repository

import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.entities.PokemonEntity
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
    // Se não tiver stats, carrega automaticamente
    suspend fun getPokemonById(id: Int): Pokemon? {
        // Tenta buscar do cache primeiro
        val cached = dao.getPokemonById(id)
        if (cached != null) {
            if (!cached.hasDetails) {
                loadPokemonStats(id)
                val updated = dao.getPokemonById(id)
                return updated?.toDomainModel()
            }
            return cached.toDomainModel()
        }

        // Se não tem no cache, busca da API completa
        return try {
            val dto = api.getPokemonDetail(id)
            val entity = dto.toEntity() // versão completa com stats
            dao.insertPokemon(entity)
            entity.toDomainModel()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun loadBasicPokemonList() {
        try {
            val response = api.getPokemonList(offset = 0, limit = 10000)
            
            // Carrega tipos de todos os pokémons em paralelo (50 por vez)
            coroutineScope {
                response.results.chunked(50).forEach { chunk ->
                    val pokemonList = chunk.mapNotNull { item ->
                        async {
                            try {
                                val id = item.url.trimEnd('/').split("/").last().toInt()
                                // Busca detalhes básicos (com tipos) mas sem stats
                                val detail = api.getPokemonDetail(id)
                                detail.toBasicEntity()
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }.awaitAll().filterNotNull()
                    
                    dao.insertAllPokemon(pokemonList)
                    kotlinx.coroutines.delay(10)
                }
            }
        } catch (e: Exception) {
            // Erro ao carregar da API
        }
    }

    // Carrega estatísticas (stats) de um pokémon específico quando necessário
    suspend fun loadPokemonStats(pokemonId: Int) {
        try {
            val existing = dao.getPokemonById(pokemonId)
            // Só carrega se não tiver stats ainda
            if (existing != null && existing.hasDetails) {
                return // já tem stats
            }
            
            val detail = api.getPokemonDetail(pokemonId)
            val fullEntity = detail.toEntity() // versão completa com stats
            dao.insertPokemon(fullEntity)
        } catch (e: Exception) {
            // Erro ao carregar stats
        }
    }

    // Busca pokémon por nome (primeiro no cache, depois na API)
    suspend fun searchPokemon(query: String): List<Pokemon> {
        val queryInt = query.toIntOrNull() ?: -1

        // Busca no cache local
        val cached = dao.searchPokemon(query, queryInt)
        if (cached.isNotEmpty()) {
            return cached.map { it.toDomainModel() }
        }

        // Se não encontrou no cache, tenta buscar na API pelo nome
        return try {
            val dto = api.getPokemonDetailByName(query.lowercase())
            val entity = dto.toEntity()
            dao.insertPokemon(entity)
            listOf(entity.toDomainModel())
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Carrega lista de Pokémon da API e salva no cache (método antigo mantido para compatibilidade)
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

    // Mapeia DTO para Entity básica (com tipos, mas sem stats)
    private fun com.example.pokedex.data.remote.dto.PokemonDetailDto.toBasicEntity(): PokemonEntity {
        val types = this.types.sortedBy { it.slot }.map { it.type.name }

        return PokemonEntity(
            id = this.id,
            name = this.name.replaceFirstChar { it.uppercase() },
            imageUrl = this.sprites.front_default ?: "",
            type1 = types.getOrNull(0) ?: "unknown",
            type2 = types.getOrNull(1),
            height = this.height,
            weight = this.weight,
            hp = 0,
            attack = 0,
            specialAttack = 0,
            specialDefense = 0,
            defense = 0,
            speed = 0,
            hasDetails = false
        )
    }

    // Mapeia DTO para Entity completa (com tipos E stats)
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
            speed = this.stats.find { it.stat.name == "speed" }?.base_stat ?: 0,
            hasDetails = true // indica que tem stats completos
        )
    }

    // Mapeia Entity para Domain Model
    private fun PokemonEntity.toDomainModel(): Pokemon {
        // Agora sempre mostra tipos (carregados na lista básica)
        val types = mutableListOf<String>()
        if (type1.isNotEmpty() && type1 != "unknown") {
            types.add(type1)
        }
        type2?.let { 
            if (it.isNotEmpty() && it != "unknown") {
                types.add(it)
            }
        }

        val finalImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/${id}.png"

        return Pokemon(
            id = this.id,
            name = this.name,
            imageUrl = finalImageUrl,
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