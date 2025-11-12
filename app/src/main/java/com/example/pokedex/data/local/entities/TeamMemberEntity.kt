package com.example.pokedex.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_team")
data class TeamMemberEntity(
    @PrimaryKey
    val pokemonId: Int,
    val name: String,
    val imageUrl: String,
    val type1: String,
    val type2: String?,
    val addedAt: Long = System.currentTimeMillis()
)