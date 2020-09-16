package com.example.pokemon.ui.main

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.util.Log
import com.example.pokemon.NavigateBack

class ConnectivityCallback(var listener:NavigateBack) : ConnectivityManager.NetworkCallback() {
     var networkState = false
    override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
        val connected = capabilities.hasCapability(NET_CAPABILITY_INTERNET)
        notifyConnectedState(connected)
    }
    private fun notifyConnectedState(connected: Boolean) {
        Log.d("HeyKome", "THIS IS $connected")

        if (connected && !networkState){
          listener.Back()
        }
    }
    override fun onLost(network: Network) {
        notifyConnectedState(false)
    }

}