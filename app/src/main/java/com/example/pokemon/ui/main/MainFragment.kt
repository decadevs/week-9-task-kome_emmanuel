package com.example.pokemon.ui.main

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.NavigateBack
import com.example.pokemon.fragment.PokemonAdapter
import com.example.pokemon.R
import com.example.pokemon.data.AllPokemon
import com.example.pokemon.repository.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: PokemonAdapter
    private lateinit var scrollListener: RecyclerViewLoadMoreScroll
    private lateinit var request: ApiService.EndpointInterface
    private var nextPage: String? = null
    private lateinit var disposable: Disposable
    internal var compositeDisposable  = CompositeDisposable()
    internal  lateinit var recycleTired:RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {


        var itemView = inflater.inflate(R.layout.main_fragment, container, false)
        activity?.window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)


        }



        if (isNetworkConnected()){
            pokemonReceiver(itemView)
        } else{
            goToNextFragment()
        }

       return itemView

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val cm = context?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
       // cm.registerDefaultNetworkCallback(ConnectivityCallback(this))
        /**added the click on the recycleItems**/
        recycleItems.addOnItemClickListener(object: OnItemClickListener {
            /**on click of the item on the recycleView it moves to the next fragment**/
            override fun onItemClicked(position: Int, view: View) {
                /**setting the bundle to take the data to the next fragment**/
                val bundle = Bundle()
                bundle.putString("pokemonId", (position + 1).toString())
                var detailsFragment = DetailsFragment()
                detailsFragment.arguments = bundle
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.container,detailsFragment)
                    addToBackStack(null)
                    commit()
                }
            }
        })
/**making the network call**/



    }
/**created a function to set onclick on every recycler view child**/
    private fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }

        })
    }


    private fun loadMorePokemon() {
        /**if the next page is null this loadMorePokemon will not proceed
         * with the call**/
        if(nextPage == null) return

        // add the loading view
        adapter.addLoadingView()

        /**making network call to get more images from the server**/
        val call = request.getMorePokemon(nextPage!!)

        call.enqueue(object: Callback<AllPokemon> {
            override fun onFailure(call: Call<AllPokemon>, t: Throwable) {
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<AllPokemon>, response: Response<AllPokemon>) {
                if(response.isSuccessful) {
                    val results = response.body()?.results

                    nextPage = response.body()!!.next

                    // remove loading view
                    adapter.removeLoadingView()

                    // add result to previous pokemon list
                    adapter.addData(results!!)

                    // change the isLoading back to false
                    scrollListener.setLoaded()

                    // update the recycleView
                    adapter.notifyDataSetChanged()

                }
            }
        })
    }

    /**create an interface for the onclick on each recycle item**/
    interface OnItemClickListener {
        fun onItemClicked(position:Int, view:View)
    }

    private fun setRVLayoutManager() {
        recycleItems.layoutManager = LinearLayoutManager(requireContext())
        recycleItems.setHasFixedSize(true)
    }

    private fun setRVScrollListener() {
        scrollListener = RecyclerViewLoadMoreScroll(LinearLayoutManager(requireContext()))
        scrollListener.setOnLoadMoreListener(object: RecyclerViewLoadMoreScroll.OnLoadMoreListener{
            override fun onLoadMore() {
                loadMorePokemon()
            }
        })

        recycleItems.addOnScrollListener(scrollListener)
    }

    // 1
//    private fun getPokemonObservable(): Observable<Response<AllPokemon>> {
//        // 2
//        return Observable.create { emitter ->
//            // 3
//            request = ApiService().build()
//            val call = request.getAllPokemon()
//
//            call.enqueue(object: Callback<AllPokemon> {
//                override fun onFailure(call: Call<AllPokemon>, t: Throwable) {
//                    Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
//
//
//                }
//
//                override fun onResponse(call: Call<AllPokemon>, response: Response<AllPokemon>) {
//                    if(response.isSuccessful) {
//                        val results = response.body()?.results
//
//                        emitter.onNext(response)
//
////                        nextPage = response.body()!!.next
////                        adapter = PokemonAdapter(results!!)
////                        adapter.notifyDataSetChanged()
////                        recycleItems.adapter = adapter
//
////                        // set recycleView manager
////                        setRVLayoutManager()
////
////                        // set recycleView scroll listener
////                        setRVScrollListener()
//                    }
//                }
//            })
//            // 5
//            emitter.setCancellable {
//                // 6
//
//            }
//        }
//    }



    fun pokemonReceiver(itemView:View){

        recycleTired = itemView.findViewById(R.id.recycleItems)
        request = ApiService().build()


        compositeDisposable.add(request.getAllPokemon()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                nextPage = it.next

                recycleTired.layoutManager = LinearLayoutManager(requireContext())
                recycleTired.setHasFixedSize(true)
                    adapter = PokemonAdapter(it.results)
                     recycleTired.adapter = adapter



                // set recycleView manager
                //setRVLayoutManager()

                // set recycleView scroll listener
               // setRVScrollListener()
            })



     }

    private fun goToNextFragment() {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.container, DisplayErrorFragment())
            addToBackStack(null)
            commit()
        }
    }

    fun isNetworkConnected(): Boolean {
        //1
        val connectivityManager = activity?.
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //2
        val activeNetwork = connectivityManager.activeNetwork
        //3
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        //4
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}


