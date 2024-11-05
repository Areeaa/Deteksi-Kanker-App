package com.dicoding.asclepius.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ItemListHistoryBinding
import com.dicoding.asclepius.local.database.Result


class HistoryAdapter(private val results: List<Result>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(private val binding: ItemListHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        
        @SuppressLint("SetTextI18n")
        fun bind(result: Result) {
            binding.apply {
                val imageUri = result.imageUri?.let { Uri.parse(it) }
                Log.d("HistoryViewHolder", "Loading image from URI: $imageUri") // Log URI for verification

                if (imageUri != null) {
                    // gunakan glide dan resize gambar
                    Glide.with(itemView.context)
                        .load(imageUri)
                        .override(224, 224)
                        .placeholder(R.drawable.ic_place_holder)
                        .error(R.drawable.ic_place_holder)
                        .into(imageView)
                } else {
                    Log.e("HistoryViewHolder", "Image URI is null or malformed")
                    imageView.setImageResource(R.drawable.ic_place_holder)
                }


                categoryTextView.text = result.category
                confidenceTextView.text = "Confidence: ${(result.confidence * 100).toInt()}%"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemListHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount() = results.size
}

