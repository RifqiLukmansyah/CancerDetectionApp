package com.dicoding.cancerdetectionapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.cancerdetectionapp.R
import com.dicoding.cancerdetectionapp.data.db.predictionhistory

class HistoryAdapter(private val predictionList: List<predictionhistory>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = predictionList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = predictionList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_history)
        val labelTextView: TextView = itemView.findViewById(R.id.tv_history_label)
        val confidenceTextView: TextView = itemView.findViewById(R.id.tv_confidence_score)
        fun bind(prediction: predictionhistory) {
            Glide.with(itemView.context)
                .load(prediction.imagePath)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_launcher_background)
                .into(imageView)

            labelTextView.text = prediction.label
            confidenceTextView.text = prediction.score
        }
    }
}
