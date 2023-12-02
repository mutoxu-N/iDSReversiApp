package com.github.mutoxu_n.idsreversiapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    data class ReversiConfig(
        val name: String,
        val height: Int,
        val width: Int,
        val board: List<Int>)

    private var _configs: MutableLiveData<List<ReversiConfig>> = MutableLiveData(null)
    val configs: LiveData<List<ReversiConfig>> get() = _configs
}