package com.obrolapp.obrol.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class UserPrefs(private val context: Context) {
    companion object {
        private val Context.counterDataStore by preferencesDataStore(
            name = "user_prefs"
        )
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val ID_USER = stringPreferencesKey("id_user")

    }

    suspend fun saveUser(idUser : String){
        context.counterDataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[ID_USER] = idUser

        }
    }

    fun isLoggin() : Flow<Boolean> {
        return context.counterDataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN] ?: false
        }
    }

    val getIdUser : Flow<String> = context.counterDataStore.data.map {
        it[ID_USER] ?: ""

    }

    suspend fun clear() {
        context.counterDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    fun isAlreadyLogin() : Boolean {
        return runBlocking {
            isLoggin().first()
        }
    }


}