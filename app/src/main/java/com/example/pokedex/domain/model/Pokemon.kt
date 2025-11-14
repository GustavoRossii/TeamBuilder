package com.example.pokedex.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val height: Float,
    val weight: Float,
    val hp: Int,
    val attack: Int,
    val specialAttack: Int,
    val specialDefense: Int,
    val defense: Int,
    val speed: Int
)