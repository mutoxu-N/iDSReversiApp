package com.github.mutoxu_n.idsreversiapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RemoteConfigureDialogViewModel: ViewModel() {
    private val _address: MutableLiveData<String> = MutableLiveData("127.0.0.1")
    val address: LiveData<String>
        get() = _address

    private val _port: MutableLiveData<Int> = MutableLiveData(443)
    val port: LiveData<Int>
        get() = _port

    fun setAddress(address: String) {
        _address.value = address
    }

    fun setPort(port: Int) {
        _port.value = port
    }
}