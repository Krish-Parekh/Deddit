package com.krish.meme.viewModel


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krish.meme.di.MemeRetro
import com.krish.meme.model.Meme
import com.krish.meme.network.PostService
import com.krish.meme.repository.DataStoreRepository
import com.krish.meme.repository.MemeDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    @MemeRetro private val postService: PostService,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
) : AndroidViewModel(application) {

    val checkChipId = MutableLiveData<Int>(0)

    fun postData(subreddit: String): Flow<PagingData<Meme>> {
        val listData =
            Pager(PagingConfig(pageSize = 10, enablePlaceholders = true, maxSize = 200)) {
                MemeDataSource(postService, subreddit)
            }.flow.catch { error ->
                Log.d(TAG, "Error : ${error.message}")
            }.cachedIn(viewModelScope)
        return listData
    }

    val tempSubredditName: MutableLiveData<String> = MutableLiveData("")
    val subredditName: MutableLiveData<String> = MutableLiveData()

    fun saveSubreddit() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveSubreddit(tempSubredditName.value!!)
        }
    }

    fun retrieveData() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.getSubreddit().collect {
                subredditName.postValue(it)
            }
        }
    }


}