package com.krish.meme.di

import com.krish.meme.Constants.Companion.CLOUD_BASE_URL

import com.krish.meme.Constants.Companion.MEME_BASE_URL
import com.krish.meme.network.NotificationService
import com.krish.meme.network.PostService
import com.krish.meme.worker.WorkerUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    @MemeRetro
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    @MemeRetro
    fun provideConverterFactory(): MoshiConverterFactory {
        return MoshiConverterFactory.create()
    }




    @Singleton
    @Provides
    @MemeRetro
    fun provideRetrofitInstance(
        @MemeRetro okHttpClient: OkHttpClient,
        @MemeRetro moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(MEME_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    @MemeRetro
    fun provideApiService(@MemeRetro retrofit: Retrofit): PostService {
        return retrofit.create(PostService::class.java)
    }

    @Singleton
    @Provides
    @Notification
    fun provideHttpClientNotify(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }


    @Singleton
    @Provides
    @Notification
    fun provideConverterFactoryNotify(): MoshiConverterFactory {
        return MoshiConverterFactory.create()
    }


    @Singleton
    @Provides
    @Notification
    fun provideRetrofitInstanceNotification(
        @Notification okHttpClient: OkHttpClient,
        @Notification moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CLOUD_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    @Notification
    fun provideApiNotification(@Notification retrofit: Retrofit): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }

    @Singleton
    @Provides
    fun provideWorkerUtil(@Notification notificationService: NotificationService, @MemeRetro postService: PostService) : WorkerUtils{
        return WorkerUtils(notificationService, postService)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MemeRetro

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Notification



