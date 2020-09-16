package com.example.pokemon.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.R
import com.squareup.picasso.Picasso
import com.example.pokemon.data.Result

class PokemonAdapter(private var pokemonItems: ArrayList<Result?>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val imageBaseUrl = "https://pokeres.bastionbot.org/images/pokemon/"
    /**declaring the View_type_item which is the recycler item to 0**/
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == VIEW_TYPE_ITEM) {
                var itemView = LayoutInflater.from(parent.context).inflate(R.layout.pokemon_list,parent, false)
                ItemViewHolder(itemView)
            }else{
                var itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_loading,parent, false)
                LoadingViewHolder(itemView)
            }
        }

        override fun getItemCount() = pokemonItems.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


            if(holder.itemViewType == VIEW_TYPE_ITEM) {

                var currentPosition = pokemonItems[position]

                var pokemonUrlAddress = currentPosition!!.url.split('/')
                var pokemonId = pokemonUrlAddress!![pokemonUrlAddress.size - 2]


                var imageContainer = holder.itemView.findViewById<ImageView>(R.id.imageIcon)
                var cardBackgroundImage = holder.itemView.findViewById<ImageView>(R.id.cardBackgroundImage)
                var pokemonName  = holder.itemView.findViewById<TextView>(R.id.name)
                pokemonName.text = currentPosition!!.name



                Picasso.get()
                    .load("${imageBaseUrl + pokemonId}.png").resize(400, 400).centerCrop()
                    .placeholder(R.drawable.pokemon_icon).resize(450, 450).centerCrop()
                    .into(cardBackgroundImage)

                Picasso.get()
                    .load("${imageBaseUrl + pokemonId}.png").resize(450, 450).centerCrop()
//                    .placeholder(R.drawable.pokemon_icon)
//                    .error(R.drawable.pokemon_icon)
                    .into(imageContainer)
            }
        }

    override fun getItemViewType(position: Int): Int {
        return if(pokemonItems[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    fun addData(dataViews: List<Result?>) {
        this.pokemonItems.addAll(dataViews)
        notifyDataSetChanged()
    }

//    fun getItemAtPosition(position: Int): Result {
//        return pokemonItems[position]!!
//    }

    fun addLoadingView() {
    // Add loading item
        pokemonItems.add(null)
        notifyItemInserted(pokemonItems.size -1)
    }


    fun removeLoadingView() {
    // Remove loading item
        if(pokemonItems.size != 0) {
            pokemonItems.removeAt(pokemonItems.size -1)
            notifyItemRemoved(pokemonItems.size)
        }
    }

/** The view holder has a constructor with an itemView as parameter and we get a reference to the views from pokemon_list layout .**/
        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

