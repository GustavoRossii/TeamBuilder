package com.example.pokedex.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_cache")
data class PokemonEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val imageUrl: String,
    val type1: String = "normal",
    val type2: String? = null,
    val height: Float = 0.0f,
    val weight: Float = 0.0f,
    val hp: Int = 0,
    val attack: Int = 0,
    val specialAttack: Int = 0,
    val specialDefense: Int = 0,
    val defense: Int = 0,
    val speed: Int = 0,
    val hasDetails: Boolean = false
)