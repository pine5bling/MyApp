package com.example.myapp

import android.app.Application
import com.example.myapp.common.setApplication

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setApplication(this)
    }
}