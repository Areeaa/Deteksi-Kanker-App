package com.dicoding.asclepius.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.dicoding.asclepius.viewmodel.NewsViewModel

class NewsActivity : AppCompatActivity() {

    private lateinit var rvNews: RecyclerView
    private lateinit var binding: ActivityNewsBinding
    private lateinit var adapter: NewsAdapter
    private val viewmodel: NewsViewModel by viewModels()

    private lateinit var progressBar : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvNews = binding.recyclerView
        adapter = NewsAdapter()


        progressBar = binding.progressBar


        rvNews.adapter = adapter
        rvNews.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        //observe event
        viewmodel.news.observe(this){news->
            adapter.submitList(news)
            progressBar.visibility = View.GONE
        }

        progressBar.visibility = View.VISIBLE
        viewmodel.loadNews()

    }
}