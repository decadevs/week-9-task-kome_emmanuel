package com.decagon.week8livedata.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroInstance {

    companion object{
        private const val BASE_URL = "https://pokeapi.co/api/v2/"

        fun getRetroInstance(): Retrofit {

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

//        val retrofitService: RetrofitService
//            get() = RetrofitClient.getClient(BASE_URL).create(RetrofitService::class.java)
    }
}