package com.dicoding.asclepius.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.entity.Result
import com.dicoding.asclepius.data.repository.ResultRepository

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val mResultRepository: ResultRepository = ResultRepository(application)

    var currentImageUri: Uri? = null


    fun insert(result: Result){
        mResultRepository.insert(result)
    }

    fun getAllResult(): LiveData<List<Result>>{
        return mResultRepository.getAllResult() }
}


