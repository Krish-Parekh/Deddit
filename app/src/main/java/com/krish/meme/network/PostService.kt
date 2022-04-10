package com.krish.meme.network

import com.krish.meme.model.Post
import retrofit2.http.GET
import retrofit2.http.Path

interface PostService {
    @GET("{subreddit}/{count}")
    suspend fun getAllPost(@Path("subreddit") name : String, @Path("count") count: Int): Post

}