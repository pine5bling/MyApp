package com.example.myapp.broadcastReceive

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.example.myapp.INetworkListener
import com.example.myapp.NetworkChange
import com.example.myapp.R
import com.example.myapp.common.STRING_DEFAULT
import com.example.myapp.common.getAppString

// lắng nghe và phản hồi
class NetworkChangeReceiver : BroadcastReceiver() {

    var listener: INetworkListener? = null

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val isWifiConnected = NetworkChange(connectivityManager).isWifiConnected()
        val isEthernetConnected = NetworkChange(connectivityManager).isEthernetConnected()

        when {
            isWifiConnected -> {
                listener?.onConnect(getAppString(R.string.connect))
                listener?.onTransportType(getAppString(R.string.wifi))
            }
            isEthernetConnected -> {
                listener?.onConnect(getAppString(R.string.connect))
                listener?.onTransportType(getAppString(R.string.ethernet))
            }
            else -> {
                listener?.onConnect(getAppString(R.string.disconnect))
                listener?.onTransportType(STRING_DEFAULT)
            }
        }
    }
}