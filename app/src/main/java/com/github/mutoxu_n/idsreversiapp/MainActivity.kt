package com.github.mutoxu_n.idsreversiapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.mutoxu_n.idsreversiapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        // binding
        binding.btStart.setOnClickListener {
            if(!viewModel.showingDialog) {
                viewModel.setDialogShowing(true)
                val dialog = BoardSelectDialogFragment.newInstance()
                dialog.show(supportFragmentManager, "BoardSelect")
            }
        }
        binding.btConfig.setOnClickListener {
            if(!viewModel.showingDialog){
                viewModel.setDialogShowing(true)
                val dialog = RemoteConfigureDialogFragment.newInstance()
                dialog.show(supportFragmentManager, "RemoteConfigure")
            }
        }

        setContentView(binding.root)
    }

    fun onDialogClosed() {
        viewModel.setDialogShowing(false)
    }
}