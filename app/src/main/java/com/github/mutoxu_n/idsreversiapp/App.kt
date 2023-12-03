package com.github.mutoxu_n.idsreversiapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class App: Application() {
    companion object {
        const val KEY_ADDRESS = "address"
        const val KEY_PORT = "port"

        private lateinit var _instance: App
        val app: App get() = _instance

        val pref: SharedPreferences
            get() = _instance.getSharedPreferences("config", Context.MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        _instance = this
    }
}