package com.dicoding.cancerdetectionapp.view.result

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.cancerdetectionapp.data.ResultState
import com.dicoding.cancerdetectionapp.data.response.ArticlesItem
import com.dicoding.cancerdetectionapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ResultViewModel : ViewModel() {
    private val _article = MutableLiveData<ResultState<List<ArticlesItem>>>()
    val article = _article

    init {
        getArticle()
    }

    private fun getArticle() {
        viewModelScope.launch {
            try {
                article.value = ResultState.Loading
                val response = ApiConfig.getApiService().getArticle()
                if (response.status == "ok") {
                    article.value = ResultState.Success(response.articles)
                }
            } catch (e: HttpException) {
                article.value = ResultState.Error(e.message())
            }
        }
    }
}