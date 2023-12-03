package com.github.mutoxu_n.idsreversiapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.github.mutoxu_n.idsreversiapp.databinding.FragmentBoardSelectDialogBinding

class BoardSelectDialogFragment: DialogFragment() {
    private lateinit var binding: FragmentBoardSelectDialogBinding
    private lateinit var viewModel: BoardSelectDialogViewModel
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>

    companion object {
        fun newInstance(): BoardSelectDialogFragment {
            return BoardSelectDialogFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // binding
        binding = FragmentBoardSelectDialogBinding.inflate(inflater)

        binding.btNext.setOnClickListener { viewModel.nextPage() }
        binding.btPrevious.setOnClickListener { viewModel.previousPage() }
        binding.btDone.setOnClickListener { GameActivity.startActivity(requireContext(), activityLauncher) }

        activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // view model
        viewModel = ViewModelProvider(this)[BoardSelectDialogViewModel::class.java]

        viewModel.configs.observe(this) { repaint() }
        viewModel.page.observe(this) { repaint() }
    }

    // 再描画
    private fun repaint() {
        val grid = binding.gird
        val config = viewModel.getConfig() ?: return

        grid.columnCount = config.width
        grid.rowCount = config.height
        grid.removeAllViews()

        for(y in 0..grid.columnCount)
            for(x in 0..grid.rowCount) {
                val img = ImageView(context)
                val params = GridLayout.LayoutParams()
                params.height = App.convertDp2Px(10f).toInt()
                params.width = params.height
                params.rowSpec = GridLayout.spec(y)
                params.columnSpec = GridLayout.spec(x)
                img.setPadding(App.convertDp2Px(1f).toInt())
                img.layoutParams = params
                when(config.board[y*config.width + x]) {
                    1 -> img.setBackgroundResource(R.drawable.black_stone)
                    2 -> img.setBackgroundResource(R.drawable.white_stone)
                    else -> img.setBackgroundResource(R.drawable.board_background)
                }
                grid.addView(img)
            }
    }
}