package com.dicoding.cancerdetectionapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_history")
data class predictionhistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String,
    val label: String,
    var score: String
)