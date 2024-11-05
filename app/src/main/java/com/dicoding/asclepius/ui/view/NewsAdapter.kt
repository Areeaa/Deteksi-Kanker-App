package com.dicoding.asclepius.ui.view


import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.databinding.NewsItemBinding
import com.dicoding.asclepius.data.remote.response.ArticlesItem

class NewsAdapter: ListAdapter<ArticlesItem, NewsAdapter.MyViewHolder>(DIFF_CALLBACK) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)

        //intent ketika diklik menuju browser
        holder.itemView.setOnClickListener{ view->
            val context = view.context
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
            context.startActivity(intent)
        }

    }

    class MyViewHolder(private val binding: NewsItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(news: ArticlesItem){
            binding.tvItemTitle.text = news.title
            binding.tvItemDescription.text = news.description
            Glide.with(binding.imgPoster.context)
                .load(news.urlToImage)
                .into(binding.imgPoster)

        }
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}