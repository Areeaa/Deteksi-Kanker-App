package com.dicoding.asclepius.data.local.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Result(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var category: String = "",
    var confidence: Float,
    var imageUri: String? = null
) : Parcelable