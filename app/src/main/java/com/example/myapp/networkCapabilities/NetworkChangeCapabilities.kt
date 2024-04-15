package com.example.myapp.networkCapabilities

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import com.example.myapp.INetworkListener
import com.example.myapp.NetworkChange
import com.example.myapp.R
import com.example.myapp.common.getAppString
import com.example.myapp.common.log


// cung cấp thông tin về khả năng kết nối mạng của device và điều chỉnh hành vi app.
// NetworkCallback chỉ khả dụng từ API level 21 (Android 5.0) trở lên

class NetworkChangeCapabilities(private val context: Context) {
    private lateinit var connectivityManager: ConnectivityManager
    var listener: INetworkListener? = null

    @SuppressLint("ObsoleteSdkInt")
    fun registerNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) // mạng được thiết lập để truy cập internet
                .build()

            connectivityManager.registerNetworkCallback(
                networkRequest,
                object : ConnectivityManager.NetworkCallback() {

                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)

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
                                listener?.onTransportType(" ")
                            }
                        }
                    }

                    override fun onLost(network: Network) {
                        super.onLost(network)
                        listener?.onConnect(getAppString(R.string.disconnect))
                        listener?.onTransportType(" ")
                    }
                })
        } else {
            log("This feature is only available on Android Nougat and above.")
        }
    }

    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(ConnectivityManager.NetworkCallback())
    }
}
