package com.dicoding.asclepius.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory private constructor(
    private val mApplication: Application
) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var INSTANCE: MainViewModelFactory? = null

        @JvmStatic
        fun getInstance(application: Application): MainViewModelFactory {
            if (INSTANCE == null){
                synchronized(MainViewModelFactory::class.java){
                    INSTANCE = MainViewModelFactory(application)
                }
            }
            return INSTANCE as MainViewModelFactory
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(mApplication) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}