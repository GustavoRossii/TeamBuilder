package com.example.pokedex.data.local.dao

import androidx.room.*
import com.example.pokedex.data.local.entities.TeamMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {

    @Query("SELECT * FROM user_team ORDER BY addedAt ASC")
    fun getTeam(): Flow<List<TeamMemberEntity>>

    @Query("SELECT COUNT(*) FROM user_team")
    suspend fun getTeamSize(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM user_team WHERE pokemonId = :pokemonId)")
    suspend fun isPokemonInTeam(pokemonId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToTeam(pokemon: TeamMemberEntity)

    @Query("DELETE FROM user_team WHERE pokemonId = :pokemonId")
    suspend fun removeFromTeam(pokemonId: Int)

    @Query("DELETE FROM user_team")
    suspend fun clearTeam()
}