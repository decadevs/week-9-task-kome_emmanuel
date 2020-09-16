package com.decagon.week8livedata.network

import com.decagon.week8livedata.model.AllPokemon
import com.decagon.week8livedata.model.PokemonDetials
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetroService {

    /**
     * Interface for retrofit REST GET operations
     */
    @GET("pokemon?offset=0&limit=1050")
    fun getPokemonList(): Call<AllPokemon>

    @GET("pokemon/{id}")
    fun getSinglePokemon(@Path("id") key: String): Call<PokemonDetials>
}