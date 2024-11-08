package com.dicoding.asclepius.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.entity.Result
import com.dicoding.asclepius.data.local.database.ResultDao
import com.dicoding.asclepius.data.local.database.ResultDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ResultRepository(application: Application) {

    private val mResultDao: ResultDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = ResultDatabase.getDatabase(application)
        mResultDao = db.resultDao()
    }

    fun insert(result: Result){
        executorService.execute{mResultDao.insert(result)}
    }


    fun getAllResult(): LiveData<List<Result>>{
        return mResultDao.getAllResult()
    }

}