package com.dicoding.cancerdetectionapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: predictionhistory)

    @Query("SELECT * FROM prediction_history")
    suspend fun getAllPredictions(): List<predictionhistory>

}