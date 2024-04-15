package com.example.myapp

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.broadcastReceive.NetworkChangeReceiver
import com.example.myapp.common.STRING_DEFAULT
import com.example.myapp.networkCapabilities.NetworkChangeCapabilities


class MainActivity : AppCompatActivity() {

    private var broadCast = NetworkChangeReceiver()
    private lateinit var tvMainFst: TextView
    private lateinit var tvMainSnd: TextView
    private lateinit var networkCapabili: NetworkChangeCapabilities

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvMainFst = findViewById(R.id.tvMainFst)
        tvMainSnd = findViewById(R.id.tvMainSnd)

//        checkNetworkByBroadcast()
        networkCapabili = NetworkChangeCapabilities(this)

    }

    override fun onResume() {
        super.onResume()
        checkNetworkByNetworkChangeCapabilities(this)
    }

    private fun checkNetworkByBroadcast() {
        tvMainSnd.text = STRING_DEFAULT
        registerReceiver(broadCast, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        broadCast.listener = object : INetworkListener {
            override fun onConnect(msg: String) {
                tvMainFst.text = msg
            }

            override fun onTransportType(msg: String) {
                tvMainSnd.text = msg
            }
        }
    }

    private fun checkNetworkByNetworkChangeCapabilities(mainActivity: MainActivity) {
        tvMainFst.text = getString(R.string.disconnect)
        tvMainSnd.text = STRING_DEFAULT

        val onConnectedCallback = { string: String ->
            mainActivity.runOnUiThread {
                tvMainFst.text = string
            }
        }
        val onTransportTypeChangedCallback = { string: String ->
            mainActivity.runOnUiThread {
                tvMainSnd.text = string
            }
        }
        networkCapabili.listener = object : INetworkListener {
            override fun onConnect(msg: String) {
                onConnectedCallback.invoke(msg)
            }

            override fun onTransportType(msg: String) {
                onTransportTypeChangedCallback(msg)
            }
        }
        networkCapabili.registerNetworkCallback()

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadCast)
        networkCapabili.unregisterNetworkCallback()
    }
}