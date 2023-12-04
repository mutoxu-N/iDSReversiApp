package com.github.mutoxu_n.idsreversiapp

import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    private var _showingDialog = false
    val showingDialog get() = _showingDialog

    fun setDialogShowing(value: Boolean) {
        _showingDialog = value
    }
}