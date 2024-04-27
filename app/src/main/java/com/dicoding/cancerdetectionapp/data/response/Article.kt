package com.dicoding.cancerdetectionapp.data.response

import com.google.gson.annotations.SerializedName

data class Article(

    @field:SerializedName("totalResults")
    val totalResults: Int,

    @field:SerializedName("articles")
    val articles: List<ArticlesItem>,

    @field:SerializedName("status")
    val status: String
)