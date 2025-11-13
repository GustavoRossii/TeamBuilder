package com.example.pokedex.domain.model

data class TeamMember(
    val pokemonId: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>
)