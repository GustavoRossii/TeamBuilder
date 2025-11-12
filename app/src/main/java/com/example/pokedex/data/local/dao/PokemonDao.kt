package com.example.pokedex.data.local.dao

import androidx.room.*
import com.example.pokedex.data.local.entities.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemon_cache ORDER BY id ASC")
    fun getAllPokemon(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon_cache WHERE id = :pokemonId")
    suspend fun getPokemonById(pokemonId: Int): PokemonEntity?

    @Query("SELECT * FROM pokemon_cache WHERE name LIKE '%' || :query || '%' OR id = :queryInt ORDER BY id ASC")
    suspend fun searchPokemon(query: String, queryInt: Int): List<PokemonEntity>

    @Query("SELECT * FROM pokemon_cache WHERE hasDetails = 0 ORDER BY id ASC LIMIT :limit")
    suspend fun getPokemonWithoutDetails(limit: Int): List<PokemonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPokemon(pokemons: List<PokemonEntity>)

    @Query("UPDATE pokemon_cache SET hasDetails = 1 WHERE id = :pokemonId")
    suspend fun markAsHasDetails(pokemonId: Int)

    @Query("DELETE FROM pokemon_cache")
    suspend fun clearCache()
}