package com.krish.meme.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krish.meme.di.MemeRetro
import com.krish.meme.model.Meme
import com.krish.meme.network.PostService
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton


class MemeDataSource @Inject constructor(@MemeRetro private val apiService: PostService, private val subreddit: String) : PagingSource<Int, Meme>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Meme> {
        return try {
            val currentCount = params.key ?: 10
            val response = apiService.getAllPost(subreddit,currentCount)
            val responsePost = mutableListOf<Meme>()
            val data = response.memes
            responsePost.addAll(data)
            val prevKey = if (currentCount == 0) null else currentCount - 10
            LoadResult.Page(
                data = responsePost,
                prevKey = prevKey,
                nextKey = currentCount + 10
            )
        }catch (e : Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Meme>): Int? {
        return null
    }

}