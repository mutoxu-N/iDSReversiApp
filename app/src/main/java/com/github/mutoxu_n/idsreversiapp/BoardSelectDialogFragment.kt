package com.github.mutoxu_n.idsreversiapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
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

    override fun onStart() {
        super.onStart()
        // 横幅設定
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // binding
        binding = FragmentBoardSelectDialogBinding.inflate(inflater)

        // buttons
        binding.btNext.setOnClickListener { viewModel.nextPage() }
        binding.btPrevious.setOnClickListener { viewModel.previousPage() }
        binding.btBlack.setOnClickListener { viewModel.getConfig()?.let {
                GameActivity.startActivity(requireContext(), activityLauncher, it, true)
        } }
        binding.btWhite.setOnClickListener { viewModel.getConfig()?.let {
            GameActivity.startActivity(requireContext(), activityLauncher, it, false)
        } }
        binding.grid.post { repaint() }
        activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // view model
        viewModel = ViewModelProvider(this)[BoardSelectDialogViewModel::class.java]
        viewModel.configs.observe(this) { repaint() }
        viewModel.page.observe(this) { repaint() }
    }

    override fun onDestroy() {
        if(activity is MainActivity) {
            (activity as MainActivity).onDialogClosed()
        }
        super.onDestroy()
    }

    private fun repaint() {
        // repaint on moving
        val grid = binding.grid
        val config = viewModel.getConfig()
        if(config == null){
            if(viewModel.failedCount < 5)
                viewModel.syncConfig()
            else {
                Toast.makeText(context, "Config cannot be got from server.", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            return
        }

        // name
        binding.tvName.text = config.name

        // arrows
        val size = viewModel.getConfigSize()
        if(size == 1) {
            binding.btNext.visibility = View.GONE
            binding.btPrevious.visibility = View.GONE
        } else {
            binding.btNext.visibility = View.VISIBLE
            binding.btPrevious.visibility = View.VISIBLE
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
                val img = ImageView(context)
                val params = GridLayout.LayoutParams()
                params.columnSpec = GridLayout.spec(c, 1f)
                params.rowSpec = GridLayout.spec(r, 1f)
                params.setMargins(App.convertDp2Px(0.8f).toInt())
                img.layoutParams = params
                when(config.board[r*config.width + c]) {
                    1 -> img.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.black_stone_with_board))
                    2 -> img.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.white_stone_with_board))
                    else -> img.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.board_background))
                }
                grid.addView(img)
            }
    }
}