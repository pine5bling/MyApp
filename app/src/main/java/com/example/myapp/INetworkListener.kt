package com.example.myapp

interface INetworkListener {
    fun onConnect(msg: String)

    fun onTransportType(msg: String)
}