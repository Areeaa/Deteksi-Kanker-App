package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.remote.response.ArticlesItem
import com.dicoding.asclepius.remote.retrofit.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewsViewModel: ViewModel() {

    private val _news = MutableLiveData<List<ArticlesItem>>()
    val news: LiveData<List<ArticlesItem>> get() = _news

    private val _error = MutableLiveData<String>()


    fun loadNews(){
        val apiService = ApiConfig.getApiService()
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response = apiService.getNews(BuildConfig.API_KEY)
                if (response.articles.isNotEmpty()){
                    _news.postValue(response.articles)
                }else{
                    _error.postValue("No news articles found")
                }
            }catch (e: Exception){
                _error.postValue("Falied to fetch news: ${e.message}")
            }

        }
    }

}

