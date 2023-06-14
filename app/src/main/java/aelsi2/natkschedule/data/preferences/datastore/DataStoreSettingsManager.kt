package aelsi2.natkschedule.data.preferences.datastore

import aelsi2.natkschedule.data.preferences.SettingsManager
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME: String = "settings"

private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

class DataStoreSettingsManager(appContext: Context) : SettingsManager {
    private val settingsDataStore = appContext.dataStore

    override val saveMainScheduleEnabled: Flow<Boolean>
        get() = settingsDataStore.data.map {
            it[SAVE_MAIN_SCHEDULE] ?: SAVE_MAIN_SCHEDULE_DEFAULT
        }

    override suspend fun setSaveMainScheduleEnabled(value: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[SAVE_MAIN_SCHEDULE] = value
        }
    }

    override val saveFavoritesEnabled: Flow<Boolean>
        get() = settingsDataStore.data.map {
            it[SAVE_FAVORITES] ?: SAVE_FAVORITES_DEFAULT
        }

    override suspend fun setSaveFavoritesEnabled(value: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[SAVE_FAVORITES] = value
        }
    }

    override val backgroundSyncEnabled: Flow<Boolean>
        get() = settingsDataStore.data.map {
            it[BACKGROUND_SYNC] ?: CACHE_IN_BACKGROUND_DEFAULT
        }

    override suspend fun setCacheBackgroundSyncEnabled(value: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[BACKGROUND_SYNC] = value
        }
    }

    override val backgroundSyncIntervalHours: Flow<Int>
        get() = settingsDataStore.data.map {
            it[BACKGROUND_SYNC_INTERVAL] ?: BACKGROUND_CACHING_INTERVAL_DEFAULT
        }

    override suspend fun setBackgroundSyncIntervalHours(value: Int) {
        settingsDataStore.edit { preferences ->
            preferences[BACKGROUND_SYNC_INTERVAL] = value
        }
    }

    override val cleanCacheOnStartupEnabled: Flow<Boolean>
        get() = settingsDataStore.data.map {
            it[CLEAN_CACHE_ON_STARTUP] ?: CLEAN_CACHE_ON_STARTUP_DEFAULT
        }

    override suspend fun setCleanCacheOnStartupEnabled(value: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[CLEAN_CACHE_ON_STARTUP] = value
        }
    }

    override val cleanCacheOnSyncEnabled: Flow<Boolean>
        get() = settingsDataStore.data.map {
            it[CLEAN_CACHE_ON_SYNC] ?: CLEAN_CACHE_ON_SYNC_DEFAULT
        }

    override suspend fun setCleanCacheOnSyncEnabled(value: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[CLEAN_CACHE_ON_SYNC] = value
        }
    }

    override val keepScheduleForDays: Flow<Int>
        get() = settingsDataStore.data.map {
            it[KEEP_SCHEDULE_FOR_DAYS] ?: KEEP_LECTURES_FOR_DAYS_DEFAULT
        }

    override suspend fun setKeepScheduleForDays(value: Int) {
        if (value < 0) {
            return
        }
        settingsDataStore.edit { preferences ->
            preferences[KEEP_SCHEDULE_FOR_DAYS] = value
        }
    }

    override suspend fun resetAll() {
        settingsDataStore.edit { preferences ->
            preferences[SAVE_MAIN_SCHEDULE] = SAVE_MAIN_SCHEDULE_DEFAULT
            preferences[SAVE_FAVORITES] = SAVE_FAVORITES_DEFAULT
            preferences[BACKGROUND_SYNC] = CACHE_IN_BACKGROUND_DEFAULT
            preferences[BACKGROUND_SYNC_INTERVAL] = BACKGROUND_CACHING_INTERVAL_DEFAULT
            preferences[CLEAN_CACHE_ON_STARTUP] = CLEAN_CACHE_ON_STARTUP_DEFAULT
            preferences[CLEAN_CACHE_ON_SYNC] = CLEAN_CACHE_ON_SYNC_DEFAULT
            preferences[KEEP_SCHEDULE_FOR_DAYS] = KEEP_LECTURES_FOR_DAYS_DEFAULT
        }
    }

    companion object {
        private val SAVE_MAIN_SCHEDULE = booleanPreferencesKey("cache_main")
        private val SAVE_FAVORITES = booleanPreferencesKey("cache_favorites")
        private val BACKGROUND_SYNC = booleanPreferencesKey("background_sync")
        private val BACKGROUND_SYNC_INTERVAL = intPreferencesKey("cache_interval")
        private val CLEAN_CACHE_ON_STARTUP = booleanPreferencesKey("cache_startup_clean")
        private val CLEAN_CACHE_ON_SYNC = booleanPreferencesKey("cache_background_clean")
        private val KEEP_SCHEDULE_FOR_DAYS = intPreferencesKey("keep_schedule_days")

        private const val SAVE_MAIN_SCHEDULE_DEFAULT: Boolean = true
        private const val SAVE_FAVORITES_DEFAULT: Boolean = true
        private const val CACHE_IN_BACKGROUND_DEFAULT: Boolean = false
        private const val BACKGROUND_CACHING_INTERVAL_DEFAULT: Int = 12
        private const val CLEAN_CACHE_ON_STARTUP_DEFAULT: Boolean = true
        private const val CLEAN_CACHE_ON_SYNC_DEFAULT: Boolean = true
        private const val KEEP_LECTURES_FOR_DAYS_DEFAULT: Int = 7
    }
}