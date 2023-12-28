package com.github.mutoxu_n.idsreversiapp

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class GameActivityViewModel : ViewModel() {
    companion object {
        private const val CONNECT_TIMEOUT = 3_000
        private const val READ_TIMEOUT = 10_000

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
    private var _connecting = false

    private var _finished = false
    val finished get() = _finished

    private var _dialogFlag: MutableLiveData<Boolean> = MutableLiveData(false)
    val dialogFlag: LiveData<Boolean> get() = _dialogFlag

    fun init(config: ReversiConfig, isBlack: Boolean) {
        if (_config.value == null) {
            _config.value = config
            _humanIsBlack = isBlack
            _board.value = config.board
            if (!isBlack) putCPU()
        }
    }

    fun put(x: Int, y: Int) {
        val config = config.value ?: return
        val width = config.width
        // おけないとき
        if (board.value!![y * width + x] != 0) return

        var putFlag = false
        val stone = if (turnIsBlack.value!!) 1 else 2
        val newBoard = board.value!!.toMutableList()

        // flip
        for (dir in DIRS) {
            val n = getReverseCount(x, y, dir)
            if (n > 0) {
                putFlag = true
                for (i in 0..n)
                    newBoard[(y + dir[1] * i) * width + (x + dir[0] * i)] = stone
            }
        }

        if (putFlag) {
            // update board and turn change
            _board.value = newBoard
            _passed = false

            // check finish
            val size = width * config.height
            var finishFlag = true
            for (i in 0 until size)
                if (newBoard[i] == 0) {
                    finishFlag = false
                    break
                }

            if (finishFlag) {
                whenGameFinished()
                return
            }
            changeTurn()

        } else {
            whenCannotPlace()
        }
    }

    fun putCPU() {
        // おけないとき
        if (!isCanPlace()) {
            whenCannotPlace()
            return
        }

        if (_connecting) return
        _connecting = true
        viewModelScope.launch {
            val name = _config.value!!.name
            val board = _board.value!!
            val width = _config.value!!.width
            val size = _config.value!!.height * width
            val stone = if (turnIsBlack.value!!) 1 else 2
            _dialogFlag.value = false

            withContext(Dispatchers.IO) {
                try {
                    // prepare data
                    val json = JSONObject()
                    json.put("name", name)
                    json.put("board", JSONArray(board))
                    json.put("stone", stone)
                    val data = json.toString().toByteArray()


                    // connect
                    val url = URL(App.getRootAddress() + "predict")
                    val con = url.openConnection() as HttpURLConnection
                    con.connectTimeout = CONNECT_TIMEOUT
                    con.readTimeout = READ_TIMEOUT
                    con.requestMethod = "POST"
                    con.setFixedLengthStreamingMode(data.size)
                    con.setRequestProperty("Content-Type", "application/json")

                    val oStream = con.outputStream
                    oStream.write(data)
                    oStream.flush()
                    oStream.close()

                    val statusCode = con.responseCode
                    if (statusCode == HttpURLConnection.HTTP_OK) {
                        val br = BufferedReader(InputStreamReader(con.inputStream))
                        val res = br.use { it.readText() }
                        br.close()

                        val resJson = JSONObject(res)
                        withContext(Dispatchers.Main) {
                            val idx = resJson.getInt("action")

                            if (idx == size) whenCannotPlace()
                            else put(idx % width, idx / width)
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        _dialogFlag.value = true
                    }

                } finally {
                    _connecting = false
                }
            }
        }
    }

    private fun changeTurn() {
        val newTurnIsBlack = !_turnIsBlack.value!!
        _turnIsBlack.value = newTurnIsBlack
        if (newTurnIsBlack == !_humanIsBlack)
            putCPU()
    }

    private fun whenGameFinished() {
        _finished = true
        _turnIsBlack.value = turnIsBlack.value!!
    }

    private fun whenCannotPlace() {
        // おけなかったとき
        if (!isCanPlace()) {
            // パス
            if (_passed) {
                whenGameFinished()
                return
            }
            _passed = true
            changeTurn()
            Toast.makeText(
                App.app.applicationContext,
                App.app.applicationContext.getString(R.string.pass), Toast.LENGTH_SHORT
            ).show()

        } else {
            Toast.makeText(
                App.app.applicationContext,
                App.app.applicationContext.getString(R.string.cannot_place_there),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isCanPlace(): Boolean {
        val config = config.value ?: return false
        val width = config.width
        val size = config.height * width

        for (idx in 0 until size)
            for (dir in DIRS)
                if (getReverseCount(idx % width, idx / width, dir) > 0)
                    return true

        return false
    }


    private fun getReverseCount(x: Int, y: Int, dir: List<Int>): Int {
        val config = config.value ?: return 0
        val width = config.width
        val height = config.height
        val board = board.value!!
        val enemyStone = if (turnIsBlack.value!!) 2 else 1

        // おけないとき
        if (board[y * width + x] != 0) return 0

        // search
        var tx = x + dir[0]
        var ty = y + dir[1]
        var count = 0
        while (tx in 0 until width && ty in 0 until height
            && board[ty * width + tx] == enemyStone
        ) {
            count++
            tx += dir[0]
            ty += dir[1]
        }

        return if (tx !in 0 until width
            || ty !in 0 until height
            || board[ty * width + tx] == 0
        ) 0 else count
    }

    fun countStone(stone: Int): Int {
        val board = board.value ?: return -1
        var count = 0
        for (s in board) if (s == stone) count++
        return count
    }

}