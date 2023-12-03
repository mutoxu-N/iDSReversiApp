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
        fun startActivity(context: Context, launcher: ActivityResultLauncher<Intent>) {
            val intent = Intent(context, GameActivity::class.java)
            launcher.launch(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[GameActivityViewModel::class.java]

        setContentView(binding.root)
    }
}