package com.dicoding.asclepius.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.fabToDetection.setOnClickListener(this)
        binding.fabToHistory.setOnClickListener(this)
        binding.fabToNews.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fab_to_detection ->{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.fab_to_history ->{
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.fab_to_news ->{
                val intent = Intent(this, NewsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}