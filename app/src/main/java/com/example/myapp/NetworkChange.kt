package com.example.myapp

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetworkChange(private val connectivityManager: ConnectivityManager) {

    private fun isNetworkConnected(transportType: Int): Boolean {
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasTransport(transportType) == true
    }

    fun isWifiConnected(): Boolean {
        return isNetworkConnected(NetworkCapabilities.TRANSPORT_WIFI)
    }

    fun isEthernetConnected(): Boolean {
        return isNetworkConnected(NetworkCapabilities.TRANSPORT_ETHERNET)
    }
}

// Broadcast: getter for activeNetworkInfo: NetworkInfo?' is deprecated. Deprecated in Java
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val netInfo = cm.activeNetworkInfo
//        //should check null because in airplane mode it will be a null
//        return netInfo != null && netInfo.isConnected