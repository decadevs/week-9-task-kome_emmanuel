package com.decagon.week8livedata.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.decagon.week8livedata.R
import com.decagon.week8livedata.viewmodel.ActivityViewModel
import kotlinx.android.synthetic.main.activity_pokemon_details.*

@Suppress("DEPRECATION")
class SinglePokemonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_details)

        getSinglePokemonDetails()
    }


    /**
     * Get unique Pokemon from api and send to view from LiveData observer
     */

    private fun getSinglePokemonDetails() {
        val pokemonTextView = findViewById<TextView>(R.id.pokemon_tv)
        val heightTv: TextView = findViewById(R.id.heightTv)
        val weightTv: TextView = findViewById(R.id.weightTv)
        val pokemonName: TextView = findViewById(R.id.pokemon_name)
        val pokemonTv1: TextView = findViewById(R.id.pokemon_tv1)
        val pokemonTv2: TextView = findViewById(R.id.pokemon_tv2)

        //Get Unique Id from intent
        val pokemonId = intent.getStringExtra("POKEMON_ID")

        val viewModel = ViewModelProviders.of(this).get(ActivityViewModel::class.java)
        viewModel.getPokemonDetailsLiveDataObserver().observe(this, Observer {

            if (it != null) {
                //Get name, height and weight
                heightTv.text = "Height: ${it.height}"
                weightTv.text = "Weight: ${it.weight}"
                pokemonName.text = it.name ?: "No name to display"

                //Concatenate moves to a textview
                var moveString = ""
                moveString += it.moves.joinToString(", ") { move ->
                    move.move.name
                } ?: "No moves to display"

                pokemonTextView.text = moveString

                //Concatenate Abilities to a textview
                var abilityString = ""
                abilityString += it.abilities.joinToString(", ") { ability ->
                    ability.ability.name
                } ?: "No Abilities to display"

                pokemonTv1.text = abilityString

                //Concatenate stats to a textview
                var statsString = ""
                statsString += it.stats.joinToString(", ") { stat ->
                    "${stat.stat.name} : ${stat.baseStat} "
                } ?: "No Stats to display"

                pokemonTv2.text = statsString

            }
        })

        //Obtain data from Api call
        viewModel.makePokemonDetailsApiCall(this)


        //Set image for each Pokemon
        val imageUrl = intent.getStringExtra("IMAGE_URL")

        getPokemonImage(imageUrl, pokemonId)


    }

    //Get each pokemon image
    private fun getPokemonImage(imageUrl: String?, pokemonId: String?) {
        if (imageUrl != "") {
            Glide.with(this@SinglePokemonActivity).load(imageUrl)
                .into(single_pokemon_iv)
        } else {
            val imageUrl = "https://pokeres.bastionbot.org/images/pokemon/$pokemonId.png"
            Glide.with(this@SinglePokemonActivity).load(imageUrl)
                .into(single_pokemon_iv)
        }
    }


}

