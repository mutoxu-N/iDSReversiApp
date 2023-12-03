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
//        activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // view model
        viewModel = ViewModelProvider(this)[BoardSelectDialogViewModel::class.java]

        viewModel.configs.observe(this) { repaint() }
        viewModel.page.observe(this) { repaint() }
    }

    private fun repaint() {
        // repaint on moving
        val grid = binding.gird
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