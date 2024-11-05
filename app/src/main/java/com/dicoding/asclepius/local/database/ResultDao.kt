package com.dicoding.asclepius.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ResultDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favouriteEvent: Result)

    @Query("SELECT * FROM Result WHERE id = :id")
    fun getResultById(id: String): LiveData<Result?>

    @Query("SELECT * FROM Result")
    fun getAllResult(): LiveData<List<Result>>
}