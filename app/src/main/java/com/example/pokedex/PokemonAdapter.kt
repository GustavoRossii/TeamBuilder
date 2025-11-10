package com.example.pokedex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PokemonAdapter(private val pokemons: List<Pokemon>) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    class PokemonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPokemon: ImageView = view.findViewById(R.id.ivPokemon)
        val tvNumero: TextView = view.findViewById(R.id.tvNumero)
        val tvNome: TextView = view.findViewById(R.id.tvNome)
        val tvTipo: TextView = view.findViewById(R.id.tvTipo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemons[position]
        holder.tvNumero.text = "#${String.format("%03d", pokemon.numero)}"
        holder.tvNome.text = pokemon.nome
        holder.tvTipo.text = pokemon.tipo
        // Por enquanto sem imagem - vamos adicionar depois
    }

    override fun getItemCount() = pokemons.size
}