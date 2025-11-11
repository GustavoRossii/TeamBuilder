package com.example.pokedex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.entities.PokemonEntity

@Database(
    entities = [PokemonEntity::class],
    version = 2, // Você incrementou a versão, certifique-se que é intencional
    exportSchema = false
)
abstract class PokedexDatabase : RoomDatabase() {

    // O Room vai implementar esta função para você
    abstract fun pokemonDao(): PokemonDao

}