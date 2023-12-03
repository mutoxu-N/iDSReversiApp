package com.github.mutoxu_n.idsreversiapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameActivityViewModel: ViewModel() {
    private val _config: MutableLiveData<ReversiConfig> = MutableLiveData(null)
    val config: LiveData<ReversiConfig> get() = _config

    private var _humanIsBlack: Boolean = true
    val humanIsBlack get() = _humanIsBlack

    fun init(config: ReversiConfig, isBlack: Boolean) {
        _config.value = config
        _humanIsBlack = isBlack
        Log.e("GameActivityViewModel", "${config.name}, ${config.width}x${config.height}\n${config.board}\nisBlack=$isBlack")
    }

}