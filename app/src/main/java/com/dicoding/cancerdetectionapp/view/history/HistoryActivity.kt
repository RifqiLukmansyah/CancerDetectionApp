package com.dicoding.cancerdetectionapp.view.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.cancerdetectionapp.R
import com.dicoding.cancerdetectionapp.adapter.HistoryAdapter
import com.dicoding.cancerdetectionapp.data.db.Appdatabase
import com.dicoding.cancerdetectionapp.data.db.predictionhistory
import com.dicoding.cancerdetectionapp.databinding.ActivityHistoryBinding
import com.dicoding.cancerdetectionapp.view.result.ResultActivity
import com.dicoding.cancerdetectionapp.view.result.ResultActivity.Companion.REQUEST_HISTORY_UPDATE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var predictionRecyclerView: RecyclerView
    private lateinit var predictionAdapter: HistoryAdapter
    private lateinit var emptytext: TextView
    private var predictionList: MutableList<predictionhistory> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_history)
        binding = ActivityHistoryBinding.inflate(layoutInflater)

        predictionRecyclerView = findViewById(R.id.historyrecycler)
        emptytext = findViewById(R.id.emptytext)
        predictionAdapter = HistoryAdapter(predictionList)
        predictionRecyclerView.adapter = predictionAdapter
        predictionRecyclerView.layoutManager = GridLayoutManager(this@HistoryActivity, 2, GridLayoutManager.VERTICAL, false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        GlobalScope.launch(Dispatchers.Main) {
            loadPredictionHistoryFromDatabase()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_HISTORY_UPDATE && resultCode == RESULT_OK) {
            GlobalScope.launch(Dispatchers.Main) {
                loadPredictionHistoryFromDatabase()
            }
        }

    }

    private fun loadPredictionHistoryFromDatabase() {
        GlobalScope.launch(Dispatchers.Main) {
            val predictions =
                Appdatabase.getDatabase(this@HistoryActivity).HistoryDao().getAllPredictions()
            Log.d(TAG, "Number of predictions: ${predictions.size}")
            predictionList.clear()
            predictionList.addAll(predictions)
            predictionAdapter.notifyDataSetChanged()

            showOrHideNoHistoryText()
        }
    }

    private fun showOrHideNoHistoryText() {
        if (predictionList.isEmpty()) {
            emptytext.visibility = View.VISIBLE
            predictionRecyclerView.visibility = View.GONE
        } else {
            emptytext.visibility = View.GONE
            predictionRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun moveToResult(imageUri: String, label: String, Score: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.IMAGE_URI, imageUri)
        intent.putExtra(ResultActivity.LABEL_TEXT, label)
        intent.putExtra(ResultActivity.SCORE_TEXT, Score)
        startActivity(intent)
    }

    companion object {
        const val TAG = "historydata"
    }
}