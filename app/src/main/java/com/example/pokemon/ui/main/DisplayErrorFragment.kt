package com.example.pokemon.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pokemon.NavigateBack
import com.example.pokemon.R


class DisplayErrorFragment : Fragment(), NavigateBack {
     lateinit var instance:DisplayErrorFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerDefaultNetworkCallback(ConnectivityCallback(this))

        instance = this
        return inflater.inflate(R.layout.fragment_display_error, container, false)
    }


   fun sendInstance():DisplayErrorFragment{

       return  this
   }


           // var gut = DisplayErrorFragment()




    override fun Back() {


            //doSomethingHere()
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.container, MainFragment())
                addToBackStack(null)
                commit()
            }




    }
}