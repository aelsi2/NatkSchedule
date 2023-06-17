package aelsi2.natkschedule.data.preferences.datastore

import aelsi2.natkschedule.data.preferences.SettingsManager
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

private const val DATASTORE_NAME: String = "settings"

private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

class DataStoreSettingsManager(appContext: Context) : SettingsManager {
    private val settingsDataStore = appContext.dataStore
    private val settingsDataStoreData: SharedFlow<Preferences> = settingsDataStore.data.shareIn(
        MainScope(), SharingStarted.Eagerly, replay = 1
    )

    override val saveMainScheduleEnabled: Flow<Boolean> = settingsDataStoreData.map {
            it[SAVE_MAIN_SCHEDULE] ?: SAVE_MAIN_SCHEDULE_DEFAULT
        }

    override suspend fun setSaveMainScheduleEnabled(value: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[SAVE_MAIN_SCHEDULE] = value
        }
    }

    override val saveFavoritesEnabled: Flow<Boolean> = settingsDataStoreData.map {
            it[SAVE_FAVORITES] ?: SAVE_FAVORITES_DEFAULT
        }

    override suspend fun setSaveFavoritesEnabled(value: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[SAVE_FAVORITES] = value
        }
    }

    override val backgroundSyncEnabled: Flow<Boolean> = settingsDataStoreData.map {
            it[BACKGROUND_SYNC] ?: CACHE_IN_BACKGROUND_DEFAULT
        }

    override suspend fun setBackgroundSyncEnabled(value: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[BACKGROUND_SYNC] = value
        }
    }

    override val backgroundSyncIntervalHours: Flow<Int> = settingsDataStoreData.map {
            it[BACKGROUND_SYNC_INTERVAL] ?: BACKGROUND_CACHING_INTERVAL_DEFAULT
        }

    override suspend fun setBackgroundSyncIntervalHours(value: Int) {
        settingsDataStore.edit { preferences ->
            preferences[BACKGROUND_SYNC_INTERVAL] = value
        }
    }

    override val cleanOldSchedulesEnabled: Flow<Boolean> = settingsDataStoreData.map {
            it[CLEAN_CACHE] ?: CLEAN_CACHE_DEFAULT
        }

    override suspend fun setCleanOldSchedulesEnabled(value: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[CLEAN_CACHE] = value
        }
    }

    override val savedScheduleMaxAgeDays: Flow<Int> = settingsDataStoreData.map {
            it[CLEAN_CACHE_AFTER] ?: CLEAN_CACHE_AFTER_DEFAULT
        }

    override suspend fun setSavedScheduleMaxAgeDays(value: Int) {
        settingsDataStore.edit { preferences ->
            preferences[CLEAN_CACHE_AFTER] = value
        }
    }

    override suspend fun resetAll() {
        settingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    override suspend fun waitForSettings() {
        settingsDataStoreData.first()
    }

    companion object {
        private val SAVE_MAIN_SCHEDULE = booleanPreferencesKey("cache_main")
        private val SAVE_FAVORITES = booleanPreferencesKey("cache_favorites")
        private val BACKGROUND_SYNC = booleanPreferencesKey("background_sync")
        private val BACKGROUND_SYNC_INTERVAL = intPreferencesKey("sync_interval")
        private val CLEAN_CACHE = booleanPreferencesKey("clean_cache")
        private val CLEAN_CACHE_AFTER = intPreferencesKey("clean_after")

        private const val SAVE_MAIN_SCHEDULE_DEFAULT: Boolean = true
        private const val SAVE_FAVORITES_DEFAULT: Boolean = true
        private const val CACHE_IN_BACKGROUND_DEFAULT: Boolean = false
        private const val BACKGROUND_CACHING_INTERVAL_DEFAULT: Int = 12
        private const val CLEAN_CACHE_DEFAULT: Boolean = true
        private const val CLEAN_CACHE_AFTER_DEFAULT: Int = 7
    }
}