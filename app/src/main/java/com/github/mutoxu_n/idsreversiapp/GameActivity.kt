package com.github.mutoxu_n.idsreversiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.core.view.setPadding
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

        viewModel.turnIsBlack.observe(this) {
            repaint()
        }

        setContentView(binding.root)
    }

    private fun repaint() {
        // repaint on moving
        val grid = binding.gird
        val config = viewModel.config.value ?: return

        // turn
        if(viewModel.turnIsBlack.value!!)
            binding.ivStone.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.black_stone))
        else
            binding.ivStone.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.white_stone))

        // grid
        grid.columnCount = config.width
        grid.rowCount = config.height
        grid.orientation = GridLayout.VERTICAL
        grid.setPadding(App.convertDp2Px(2f).toInt())
        grid.removeAllViews()
        val cellSize = grid.width / config.width

        for(r in 0 until grid.rowCount)
            for(c in 0 until grid.columnCount) {
                val img = ImageView(applicationContext)
                val params = GridLayout.LayoutParams()
                val idx = r*config.width + c
                params.width = cellSize
                params.height = cellSize
                params.setGravity(Gravity.CENTER)
                params.rowSpec = GridLayout.spec(r)
                params.columnSpec = GridLayout.spec(c)
                params.setMargins(App.convertDp2Px(0.8f).toInt())
                img.layoutParams = params
                when(viewModel.board.value!![idx]) {
                    1 -> img.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.black_stone))
                    2 -> img.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.white_stone))
                    else -> {
                        img.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.board_background))
                        img.setOnClickListener { viewModel.put(c, r) }
                    }
                }
                grid.addView(img, idx)
            }
    }
}