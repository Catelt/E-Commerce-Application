package com.goldenowl.ecommerceapp.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log


internal object NetworkHelper {
    fun isNetworkAvailable(context: Context): Boolean {
        var result: Boolean
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            result = checkConnection(this, this.activeNetwork)
        }
        return result
    }

    private fun checkConnection(
        connectivityManager: ConnectivityManager,
        network: Network?
    ): Boolean {
        connectivityManager.getNetworkCapabilities(network)?.also {
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
}