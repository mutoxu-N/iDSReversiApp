package com.github.mutoxu_n.idsreversiapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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

        // Viewのサイズが確定してから再描画
        binding.grid.post { repaint() }

        // Player Name
        if(viewModel.humanIsBlack) {
            binding.playerBlack.text = getString(R.string.you)
            binding.playerWhite.text = getString(R.string.cpu)

        } else {
            binding.playerBlack.text = getString(R.string.cpu)
            binding.playerWhite.text = getString(R.string.you)
        }

        viewModel.turnIsBlack.observe(this) { repaint() }

        setContentView(binding.root)
    }


    private fun repaint() {
        // repaint on moving
        val grid = binding.grid
        val config = viewModel.config.value ?: return

        // turn
        if(viewModel.finished) {
            binding.ivStone.visibility = View.GONE
            binding.turnDisplay.text = getString(R.string.game_finished)
            binding.detailDisplay.visibility = View.VISIBLE
            binding.detailDisplay.text =
                getString(R.string.result_stone, viewModel.countStone(1), viewModel.countStone(2))

        } else {
            if(viewModel.turnIsBlack.value!!)
                binding.ivStone.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.black_stone_with_border))
            else
                binding.ivStone.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.white_stone_with_border))
        }

        // grid
        grid.removeAllViews()
        grid.rowCount = config.height
        grid.columnCount = config.width
        grid.orientation = GridLayout.VERTICAL
        (grid.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "${config.width.toFloat() / config.height}"
        grid.setPadding(App.convertDp2Px(2f).toInt())

        for(r in 0 until grid.rowCount)
            for(c in 0 until grid.columnCount) {
                val img = ImageView(applicationContext)
                val params = GridLayout.LayoutParams()
                params.columnSpec = GridLayout.spec(c, 1f)
                params.rowSpec = GridLayout.spec(r, 1f)
                params.setMargins(App.convertDp2Px(0.8f).toInt())
                img.layoutParams = params
                when(viewModel.board.value!![r*config.width + c]) {
                    1 -> img.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.black_stone_with_board))
                    2 -> img.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.white_stone_with_board))
                    else -> {
                        img.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.board_background))
                        if(viewModel.humanIsBlack == viewModel.turnIsBlack.value!!)
                            img.setOnClickListener { viewModel.put(c, r) }

                    }
                }
                grid.addView(img)
            }
    }
}