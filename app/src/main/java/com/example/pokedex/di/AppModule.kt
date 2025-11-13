package com.example.pokedex.di

import android.content.Context
import androidx.room.Room
import com.example.pokedex.data.local.PokedexDatabase
import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.dao.TeamDao
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.data.repository.PokemonRepository
import com.example.pokedex.data.repository.TeamRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePokeApiService(): PokeApiService {
        return Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePokedexDatabase(@ApplicationContext context: Context): PokedexDatabase {
        return Room.databaseBuilder(
            context,
            PokedexDatabase::class.java,
            "pokedex_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePokemonDao(database: PokedexDatabase): PokemonDao {
        return database.pokemonDao()
    }

    @Provides
    @Singleton
    fun provideTeamDao(database: PokedexDatabase): TeamDao {
        return database.teamDao()
    }

    @Provides
    @Singleton
    fun providePokemonRepository(
        api: PokeApiService,
        dao: PokemonDao
    ): PokemonRepository {
        return PokemonRepository(api, dao)
    }

    @Provides
    @Singleton
    fun provideTeamRepository(
        teamDao: TeamDao,
        pokemonDao: PokemonDao
    ): TeamRepository {
        return TeamRepository(teamDao, pokemonDao)
    }
}