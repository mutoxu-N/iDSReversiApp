package com.github.mutoxu_n.idsreversiapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import com.github.mutoxu_n.idsreversiapp.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var viewModel: GameActivityViewModel

    companion object {
        private const val PARAM_NAME = "name"
        private const val PARAM_HEIGHT = "height"
        private const val PARAM_WIDTH = "width"
        private const val PARAM_INIT = "init"
        private const val PARAM_IS_BLACK = "is_black"

        fun startActivity(context: Context, launcher: ActivityResultLauncher<Intent>, config: ReversiConfig, isBlack: Boolean) {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(PARAM_NAME, config.name)
            intent.putExtra(PARAM_WIDTH, config.width)
            intent.putExtra(PARAM_HEIGHT, config.height)
            intent.putExtra(PARAM_INIT, config.board.toIntArray())
            intent.putExtra(PARAM_IS_BLACK, isBlack)
            launcher.launch(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init
        binding = ActivityGameBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[GameActivityViewModel::class.java]

        // get args
        val conf = ReversiConfig(
            intent.getStringExtra(PARAM_NAME)!!,
            intent.getIntExtra(PARAM_HEIGHT, -1),
            intent.getIntExtra(PARAM_WIDTH, -1),
            intent.getIntArrayExtra(PARAM_INIT)!!.toList()
        )
        viewModel.init(conf, intent.getBooleanExtra(PARAM_IS_BLACK, true))

        setContentView(binding.root)
    }
}