package com.dicoding.cancerdetectionapp.view.result

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.cancerdetectionapp.R
import com.dicoding.cancerdetectionapp.adapter.ArticleAdapter
import com.dicoding.cancerdetectionapp.data.ResultState
import com.dicoding.cancerdetectionapp.data.db.Appdatabase
import com.dicoding.cancerdetectionapp.data.db.predictionhistory
import com.dicoding.cancerdetectionapp.data.response.ArticlesItem
import com.dicoding.cancerdetectionapp.databinding.ActivityResultBinding
import com.dicoding.cancerdetectionapp.helper.ImageClassifierHelper
import com.dicoding.cancerdetectionapp.view.history.HistoryActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.io.FileOutputStream

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val viewModel: ResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUriString = intent.getStringExtra(IMAGE_URI)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            displayImage(imageUri)

            val imageClassifierHelper = ImageClassifierHelper(
                contextValue = this,
                classifierListenerValue = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(errorMessage: String) {
                        Log.d(TAG, "Error: $errorMessage")
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let { showResults(it) }
                    }
                }
            )
            imageClassifierHelper.classifyImage(imageUri)
        } else {
            Log.e(TAG, "No image URI provided")
            finish()
        }

        binding.saveButton.setOnClickListener {
            val imageUriString = intent.getStringExtra(IMAGE_URI)
            val label = binding.resulttext.text.toString()
            val score = binding.tvconfidencescore.text.toString()

            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                showToast(getString(R.string.inforesultsaved))
                savePredictionToHistory(imageUri, label, score)
            } else {
                showToast("No image URI provided")
                finish()
            }
        }

        viewModel.article.observe(this) { article ->
            when (article) {
                is ResultState.Loading -> {
                    binding.shimmerLayout.startShimmer()
                }

                is ResultState.Success -> {
                    showRecyclerView()
                    setArticleData(article.data)
                }

                is ResultState.Error -> {
                    Log.e("ResultActivity", "Error: $article")
                    Toast.makeText(this, "Error: $article", Toast.LENGTH_SHORT).show()
                }

                else -> {
                }
            }
        }
        val adapter = ArticleAdapter()
        binding.rvarticle.apply {
            layoutManager =
                LinearLayoutManager(this@ResultActivity, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
        }
    }

    private fun setArticleData(consumer: List<ArticlesItem>) {
        val adapter = ArticleAdapter()
        adapter.submitList(consumer)
        binding.rvarticle.adapter = adapter
    }

    private fun showRecyclerView() {
        binding.shimmerLayout.apply {
            stopShimmer()
            visibility = View.GONE
        }
        binding.rvarticle.visibility = View.VISIBLE
    }

    private fun displayImage(uri: Uri) {
        Log.d(TAG, "Displaying image: $uri")
        binding.resultImage.setImageURI(uri)
    }

    private fun showResults(results: List<Classifications>) {
        val topResult = results[0]
        val label = topResult.categories[0].label
        val score = topResult.categories[0].score

        fun Float.formatToString(): String {
            return String.format("%.2f%%", this * 100)
        }
        binding.resulttext.text = label
        binding.tvconfidencescore.text = score.formatToString()
        adjustResultColor(label)
        showDescriptionResult(label)
    }

    private fun savePredictionToHistory(imageUri: Uri, label: String, score: String) {
        if (label.isNotEmpty() and score.isNotEmpty()) {
            val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
            val destinationUri = Uri.fromFile(File(cacheDir, fileName))
            contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(File(cacheDir, fileName)).use { output ->
                    input.copyTo(output)
                }
            }
            val prediction = predictionhistory(
                imagePath = destinationUri.toString(),
                label = label,
                score = score
            )
            GlobalScope.launch(Dispatchers.IO) {
                val database = Appdatabase.getDatabase(applicationContext)
                try {
                    database.HistoryDao().insertPrediction(prediction)
                    Log.d(TAG, "Prediction saved successfully: $prediction")
                    val predictions = database.HistoryDao().getAllPredictions()
                    Log.d(TAG, "All predictions after save: $predictions")
                    moveToHistory(destinationUri, label, score)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to save prediction: $prediction", e)
                }
            }
        } else {
            Log.e(TAG, "Result is empty, cannot save prediction to database.")
        }
    }

    private fun moveToHistory(imageUri: Uri, label: String, score: String) {
        val intent = Intent(this, HistoryActivity::class.java)
        intent.putExtra(LABEL_TEXT, label)
        intent.putExtra(SCORE_TEXT, score)
        intent.putExtra(IMAGE_URI, imageUri.toString())
        setResult(RESULT_OK, intent)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun adjustResultColor(label: String) {
        val color = when (label.trim()) {
            "Non Cancer" -> R.color.green
            "Cancer" -> R.color.red
            else -> R.color.red
        }
        binding.resulttext.setTextColor(resources.getColor(color, null))
        binding.tvconfidencescore.setTextColor(resources.getColor(color, null))
    }

    private fun showDescriptionResult(label: String) {
        val description = when (label.trim()) {
            "Non Cancer" -> getString(R.string.noncancerdescription)
            "Cancer" -> getString(R.string.cancerdescription)
            else -> getString(R.string.cancerdescription)
        }
        binding.tvResultDescription.text = description
    }

    companion object {
        const val IMAGE_URI = "img_uri"
        const val TAG = "imagePicker"
        const val LABEL_TEXT = "label_text"
        const val SCORE_TEXT = "score_text"
        const val REQUEST_HISTORY_UPDATE = 1
    }
}