package com.github.mutoxu_n.idsreversiapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class BoardSelectDialogViewModel: ViewModel() {
    companion object {
        private const val CONNECT_TIMEOUT = 3_000
        private const val READ_TIMEOUT = 10_000
    }

    init {
        syncConfig()
    }

    private var _configs: MutableLiveData<List<ReversiConfig>> = MutableLiveData(null)
    val configs: LiveData<List<ReversiConfig>> get() = _configs

    private var _page: MutableLiveData<Int> = MutableLiveData(0)
    val page: LiveData<Int> get() = _page

    private var _failedCount = 0
    val failedCount get() = _failedCount

    fun syncConfig() {
        viewModelScope.launch {
            val list = mutableListOf<ReversiConfig>()
            withContext(Dispatchers.IO) {
                try {
                    val url = URL(App.getRootAddress() + "boards")
                    val con = url.openConnection() as HttpURLConnection
                    con.connectTimeout = CONNECT_TIMEOUT
                    con.readTimeout = READ_TIMEOUT
                    con.requestMethod = "GET"
                    con.connect()

                    // 接続先で処理に異常があったら終了
                    if(con.responseCode != HttpURLConnection.HTTP_OK) {
                        Log.e("BoardSelectDialog.kt syncConfig()", "レスポンスコード: ${con.responseCode}")
                    }

                    val str = con.inputStream.bufferedReader(Charsets.UTF_8).use {br ->
                        br.readLines().joinToString("")
                    }
                    con.disconnect()

                    val json = JSONObject(str)
                    var t: JSONObject?
                    var init: JSONArray?
                    var board: MutableList<Int>?
                    for(name in json.keys()) {
                        // モデル読み込み
                        t = json.getJSONObject(name)
                        init = t.getJSONArray("init")
                        board = mutableListOf()
                        for(i in 0 until init.length())
                            board.add(init.getInt(i))

                        // リストに追加
                        list.add(ReversiConfig(name, t.getInt("width"), t.getInt("height"), board))
                        withContext(Dispatchers.Main) {
                            _configs.value = list
                            _failedCount = 0
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    _failedCount += 1
                }

            }
        }
    }

    fun getConfig(): ReversiConfig? {
        return try { configs.value!![page.value!!] }
               catch(e: Exception) { null }
    }

    fun getConfigSize(): Int {
        return configs.value?.size ?: -1
    }

    fun nextPage() {
        val now = _page.value
        val configs = configs.value
        if(now == null || configs == null) return

        _page.value = (now + 1) % configs.size
    }

    fun previousPage() {
        val now = _page.value
        val configs = configs.value
        if(now == null || configs == null) return

        _page.value = (now - 1) % configs.size
    }
}