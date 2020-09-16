package com.example.pokemon.ui.main


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.example.pokemon.R
import com.example.pokemon.data.PokemonDetail
import com.example.pokemon.repository.ApiService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: View
    private val imageBaseUrl = "https://pokeres.bastionbot.org/images/pokemon/"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        activity?.window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        viewModel = inflater.inflate(R.layout.fragment_details, container, false)

        return viewModel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        /**receiving the id of the pokemon id **/
        val pokemonId = this.arguments?.getString("pokemonId")

        /**making network call**/
        val request = ApiService().build()
        val call =  request.getPokemon(pokemonId.toString())

        call?.enqueue(object: Callback<PokemonDetail> {
            /**throws an error message if there is failure while making the network call**/
            override fun onFailure(call: Call<PokemonDetail>, t: Throwable) {
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<PokemonDetail>, response: Response<PokemonDetail>) {
                if(response.isSuccessful) {
                    val result = response.body()
                    val pokemonImage = viewModel.findViewById<ImageView>(R.id.pokemonImage)
                    val headerBackgroundImage = viewModel.findViewById<ImageView>(R.id.headerBackgroundImage)
                    /**getting the images**/
                    Picasso.get()
                        .load("${imageBaseUrl + pokemonId}.png").resize(500, 500).centerCrop()
                        .into(pokemonImage)

                    Picasso.get()
                        .load("${imageBaseUrl + pokemonId}.png").resize(600, 600).centerCrop()
                        .into(headerBackgroundImage)

                    /**getting the individual pokemon Id**/
                    val pokemon_id = viewModel.findViewById<TextView>(R.id.pokemonId)
                   pokemon_id.text = result?.id.toString()

                    /**getting the individual pokemon name**/
                    val pokemon_name = viewModel.findViewById<TextView>(R.id.pokemonName)
                    pokemon_name.text = result?.name

                    /**getting the individual pokemon height**/
                    val pokemon_height = viewModel.findViewById<TextView>(R.id.pokemonHeight)
                    pokemon_height.text = result?.height.toString()

                    /**getting the individual pokemon Weight**/
                    pokemonWeight.text = result?.weight.toString()

                    /**getting the different views of individual pokemon**/
                    val spriteBackDefault = result?.sprites?.backDefault
                    Picasso.get()
                        .load(spriteBackDefault).resize(230,200).centerCrop().into(spriteHolder_1)

                    val spriteFrontDefault = result?.sprites?.frontDefault
                    Picasso.get()
                        .load(spriteFrontDefault).resize(230,200).centerCrop().into(spriteHolder_2)

                    val spriteBackShiny = result?.sprites?.backShiny
                    Picasso.get()
                        .load(spriteBackShiny).resize(230,200).centerCrop().into(spriteHolder_3)

                    val spriteFrontShinyFemale = result?.sprites?.frontShiny
                    Picasso.get()
                        .load(spriteFrontShinyFemale).resize(230,200).centerCrop().into(spriteHolder_4)

                    /**getting the individual pokemon order**/
                    PokemonOrder.text = result?.order.toString()

                    /**getting the individual pokemon moves**/
                    pokemonMoves.setOnClickListener{
                        val popupMenu: PopupMenu = PopupMenu(requireActivity(), pokemonMoves)
                        popupMenu.menuInflater.inflate(R.menu.menu_list,popupMenu.menu)
                        popupMenu.menu.clear()
                        result?.moves?.forEach {popupMenu.menu.add(it.move.name)}
                        popupMenu.show()
                    }

                    /**getting the individual pokemon Abilities**/
                    pokemonAbilities.setOnClickListener{
                        val popupMenu: PopupMenu = PopupMenu(requireActivity(), pokemonAbilities)
                        popupMenu.menuInflater.inflate(R.menu.menu_list,popupMenu.menu)
                        popupMenu.menu.clear()
                        result?.abilities?.forEach {popupMenu.menu.add(it.ability.name)}
                        popupMenu.show()
                    }

                    /**getting the individual pokemon Stats**/
                    pokemonStats.setOnClickListener{
                        val popupMenu: PopupMenu = PopupMenu(requireActivity(), pokemonStats)
                        popupMenu.menuInflater.inflate(R.menu.menu_list,popupMenu.menu)
                        popupMenu.menu.clear()
                        result?.stats?.forEach {popupMenu.menu.add(it.stat.name)}
                        popupMenu.show()
                    }
                }
            }
        })


    }


}