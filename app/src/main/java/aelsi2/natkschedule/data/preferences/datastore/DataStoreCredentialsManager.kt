package aelsi2.natkschedule.data.preferences.datastore

import aelsi2.natkschedule.data.preferences.CredentialsManager
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME : String = "credentials"

private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

class DataStoreCredentialsManager(appContext : Context) : CredentialsManager {
    private val credentialsDataStore = appContext.dataStore

    override val username : Flow<String?>
        get() = credentialsDataStore.data.map { it[USERNAME] }
    override suspend fun storeUsername(value : String?) {
        credentialsDataStore.edit {preferences ->
            if (value == null) {
                preferences.remove(USERNAME)
            }
            else {
                preferences[USERNAME] = value
            }
        }
    }

    override val password : Flow<String?>
        get() = credentialsDataStore.data.map { it[PASSWORD] }
    override suspend fun storePassword(value : String?) {
        credentialsDataStore.edit {preferences ->
            if (value == null) {
                preferences.remove(PASSWORD)
            }
            else {
                preferences[PASSWORD] = value
            }
        }
    }

    companion object {
        private val USERNAME = stringPreferencesKey("username")
        private val PASSWORD = stringPreferencesKey("password")
    }
}