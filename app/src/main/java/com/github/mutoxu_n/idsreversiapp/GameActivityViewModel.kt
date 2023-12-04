package com.github.mutoxu_n.idsreversiapp

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameActivityViewModel: ViewModel() {
    companion object {
        private val DIRS = listOf(
            listOf(1, 0),
            listOf(1, 1),
            listOf(0, 1),
            listOf(-1, 1),
            listOf(-1, 0),
            listOf(-1, -1),
            listOf(0, -1),
            listOf(1, -1),
        )
    }

    private val _config: MutableLiveData<ReversiConfig> = MutableLiveData(null)
    val config: LiveData<ReversiConfig> get() = _config

    private val _board: MutableLiveData<List<Int>> = MutableLiveData(null)
    val board: LiveData<List<Int>> get() = _board

    private var _humanIsBlack: Boolean = true
    val humanIsBlack get() = _humanIsBlack

    private val _turnIsBlack: MutableLiveData<Boolean> = MutableLiveData(true)
    val turnIsBlack: LiveData<Boolean> get() = _turnIsBlack

    private var _passed = false

    fun init(config: ReversiConfig, isBlack: Boolean) {
        _config.value = config
        _humanIsBlack = isBlack
        _board.value = config.board
        Log.e("GameActivityViewModel", "${config.name}, ${config.width}x${config.height}\n${config.board}\nisBlack=$isBlack")
    }

    fun put(x: Int, y: Int) {
        val config = config.value ?: return
        val width = config.width
        // おけないとき
        if(board.value!![y*width + x] != 0) return

        var putFlag = false
        val stone = if(turnIsBlack.value!!) 1 else 2
        val newBoard = board.value!!.toMutableList()

        // flip
        for(dir in DIRS) {
            val n = getReverseCount(x, y, dir)
            if(n > 0) {
                putFlag = true
                for(i in 0 ..n)
                    newBoard[(y+dir[1]*i)*width + (x+dir[0]*i)] = stone
            }
        }

        if(putFlag){
            // update board and turn change
            _board.value = newBoard
            _turnIsBlack.value = !_turnIsBlack.value!!
            _passed = false

            // check finish
            val size = width * config.height
            var finishFlag = true
            for(i in 0 until size)
                if(newBoard[i] == 0) {
                    finishFlag = false
                    break
                }
            if(finishFlag) {
                gameFinished()
                return
            }


        } else {
            // おけなかったとき
            if(!isCanPlace()) {
                // パス
                if(_passed) {
                    gameFinished()
                    return
                }
                _passed = true
                _turnIsBlack.value = !_turnIsBlack.value!!
                Toast.makeText(App.app.applicationContext, "Pass!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(App.app.applicationContext, "Cannot place there!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun gameFinished() {
        Toast.makeText(App.app.applicationContext, "Game Finished!", Toast.LENGTH_SHORT).show()
    }

    private fun isCanPlace(): Boolean {
        val config = config.value ?: return false
        val width = config.width
        val size = config.height * width

        for(idx in 0 until size)
            for(dir in DIRS)
                if(getReverseCount(idx%width, idx/width, dir) > 0){
                    Log.e("GAVM", "$idx")
                    return true
                }

        return false
    }


    private fun getReverseCount(x: Int, y: Int, dir: List<Int>): Int {
        val config = config.value ?: return 0
        val width = config.width
        val height = config.height
        val board = board.value!!
        val enemyStone = if(turnIsBlack.value!!) 2 else 1

        // おけないとき
        if(board[y*width + x] != 0) return 0

        // search
        var tx = x + dir[0]
        var ty = y + dir[1]
        var count = 0
        while(tx in 0 until width && ty in 0 until height
            && board[ty*width + tx] == enemyStone) {
            count++
            tx += dir[0]
            ty += dir[1]
        }

        return if(tx !in 0 until width
            || ty !in 0 until height
            || board[ty*width + tx] == 0) 0 else count
    }

}