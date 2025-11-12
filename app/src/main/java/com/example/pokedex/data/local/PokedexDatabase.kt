package com.example.pokedex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.dao.TeamDao
import com.example.pokedex.data.local.entities.PokemonEntity
import com.example.pokedex.data.local.entities.TeamMemberEntity

@Database(
    entities = [PokemonEntity::class, TeamMemberEntity::class],
    version = 3,  // ← AUMENTOU PARA 3
    exportSchema = false
)
abstract class PokedexDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun teamDao(): TeamDao
}