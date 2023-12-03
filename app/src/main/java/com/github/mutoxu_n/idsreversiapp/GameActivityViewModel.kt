package com.github.mutoxu_n.idsreversiapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameActivityViewModel: ViewModel() {
    private val _config: MutableLiveData<ReversiConfig> = MutableLiveData(null)
    val config: LiveData<ReversiConfig> get() = _config

    private val _board: MutableLiveData<List<Int>> = MutableLiveData(null)
    val board: LiveData<List<Int>> get() = _board

    private var _humanIsBlack: Boolean = true
    val humanIsBlack get() = _humanIsBlack

    private var _turnIsBlack: LiveData<Boolean> = MutableLiveData(true)
    val turnIsBlack: LiveData<Boolean> get() = _turnIsBlack

    fun init(config: ReversiConfig, isBlack: Boolean) {
        _config.value = config
        _humanIsBlack = isBlack
        _board.value = config.board
        Log.e("GameActivityViewModel", "${config.name}, ${config.width}x${config.height}\n${config.board}\nisBlack=$isBlack")
    }

    fun put(x: Int, y: Int) {

    }

}