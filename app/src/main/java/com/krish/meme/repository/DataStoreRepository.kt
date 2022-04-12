package com.krish.meme.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map


const val DATASTORE_NAME = "MEME_DATASTORE"
private val Context.datastore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)

class DataStoreRepository(private val context: Context) {

    companion object {
        val NAME = stringPreferencesKey("NAME")
    }

    suspend fun saveSubreddit(subreddit: String) {
        context.datastore.edit {
            it[NAME] = subreddit
        }
    }

    fun getSubreddit() =
        context.datastore.data.map {
            it[NAME]?:"meme"
        }



}