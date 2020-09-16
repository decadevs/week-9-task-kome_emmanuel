package com.example.pokemon.repository

import com.example.pokemon.data.AllPokemon
import com.example.pokemon.data.PokemonDetail
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

class ApiService {
    /**this is the base URL**/
    private val baseUrl = "https://pokeapi.co/api/v2/"
    /** OKHttp client **/
    private val client = OkHttpClient.Builder().build()
    /**retrofit instance(singleton)**/
    private val retrofit = Retrofit.Builder().baseUrl(baseUrl)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                    .client(client).build()

    fun build(): EndpointInterface {
       return retrofit.create(EndpointInterface::class.java)
    }
/**interface contains methods we are going to use to execute HTTP requests **/
    interface EndpointInterface {
        @GET("pokemon/{name}")
        fun getPokemon(@Path("name") key: String): Call<PokemonDetail>

        @GET("pokemon")
        fun getAllPokemon(): Observable<AllPokemon>

        @GET
        fun getMorePokemon(@Url url: String): Call<AllPokemon>
    }
}