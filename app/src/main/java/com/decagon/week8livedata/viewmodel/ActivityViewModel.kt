package com.decagon.week8livedata.viewmodel

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.decagon.week8livedata.R
import com.decagon.week8livedata.model.AllPokemon
import com.decagon.week8livedata.model.PokemonDetials
import com.decagon.week8livedata.network.RetroInstance
import com.decagon.week8livedata.network.RetroService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityViewModel : ViewModel() {
    lateinit var progressBar: ProgressBar
    lateinit var textView: TextView

     // Instantiate Mutable live data for recyclerview
    var recyclerListData: MutableLiveData<AllPokemon> = MutableLiveData()


     // Instantiate Mutable live data for Pokemon details
    var pokemonDetailsData: MutableLiveData<PokemonDetials> = MutableLiveData()

    //Retrofit instance of Service
    private val retroInstance: RetroService =
        RetroInstance.getRetroInstance().create(RetroService::class.java)

    fun getRecyclerLiveDataObserver(): MutableLiveData<AllPokemon> {
        return recyclerListData
    }


    //Pokemon details LiveData observer
    fun getPokemonDetailsLiveDataObserver(): MutableLiveData<PokemonDetials> {
        return pokemonDetailsData
    }


    /**
     *  API call for all pokemon
     */

    fun makeAllPokemonApiCall(context: Context) {
        progressBar = (context as Activity).findViewById(R.id.progressBar)
        textView = (context as Activity).findViewById(R.id.textView)

        progressBar.visibility = View.VISIBLE
        textView.visibility = View.VISIBLE

        val call = retroInstance.getPokemonList()

        //Retrofit call response
        call.enqueue(object : retrofit2.Callback<AllPokemon> {
            override fun onFailure(call: Call<AllPokemon>, t: Throwable) {
                recyclerListData.postValue(null)
            }

            override fun onResponse(call: Call<AllPokemon>, response: Response<AllPokemon>) {
                //Make progress bar and poor network message views to GONE
                progressBar.visibility = View.GONE
                textView.visibility = View.GONE

                if (response.code() == 200) {
                    //pass response to LiveData object
                    recyclerListData.postValue(response.body())

                } else {
                    //pass null if there's no 200 code response
                    recyclerListData.postValue(null)
                }
            }
        })
    }

    /**
     * API call for single Pokemon details
     */

    fun makePokemonDetailsApiCall(context: Context) {
        val pokemonId = (context as Activity).intent.getStringExtra("POKEMON_ID")
        val call = retroInstance.getSinglePokemon("$pokemonId")

        call.enqueue(object : Callback<PokemonDetials> {
            override fun onFailure(call: Call<PokemonDetials>, t: Throwable) {
                pokemonDetailsData.postValue(null)
            }

            override fun onResponse(
                call: Call<PokemonDetials>,
                response: Response<PokemonDetials>
            ) {
                if (response.code() == 200) {
                  pokemonDetailsData.postValue(response.body())
                } else {
                    pokemonDetailsData.postValue(null)
                }

            }

        })

    }
}