package aelsi2.natkschedule.data.preferences.datastore

import aelsi2.natkschedule.data.preferences.SettingsManager
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME : String = "settings"

private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

//TODO Отлавливание исключений, если возможно
class DataStoreSettingsManager(appContext : Context) : SettingsManager {
    private val settingsDataStore = appContext.dataStore

    override val cacheMainScheduleEnabled : Flow<Boolean>
        get() = settingsDataStore.data.map {
            it[CACHE_MAIN_SCHEDULE] ?: CACHE_MAIN_SCHEDULE_DEFAULT
        }
    override suspend fun setCacheMainScheduleEnabled(value : Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[CACHE_MAIN_SCHEDULE] = value
        }
    }

    override val cacheFavoriteSchedulesEnabled : Flow<Boolean>
        get() = settingsDataStore.data.map {
            it[CACHE_FAVORITE_SCHEDULES] ?: CACHE_FAVORITE_SCHEDULES_DEFAULT
        }
    override suspend fun setCacheFavoriteSchedulesEnabled(value : Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[CACHE_FAVORITE_SCHEDULES] = value
        }
    }

    override val cacheInBackgroundEnabled : Flow<Boolean>
        get() = settingsDataStore.data.map {
            it[CACHE_IN_BACKGROUND] ?: CACHE_IN_BACKGROUND_DEFAULT
        }
    override suspend fun setCacheInBackgroundEnabled(value : Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[CACHE_IN_BACKGROUND] = value
        }
    }

    override val backgroundCachingIntervalSeconds : Flow<Long>
        get() = settingsDataStore.data.map {
            it[BACKGROUND_CACHING_INTERVAL] ?: BACKGROUND_CACHING_INTERVAL_DEFAULT
        }
    override suspend fun setBackgroundCachingIntervalSeconds(value : Long) {
        settingsDataStore.edit { preferences ->
            preferences[BACKGROUND_CACHING_INTERVAL] = value
        }
    }

    override val cleanLectureCacheAutomatically: Flow<Boolean>
        get() = settingsDataStore.data.map {
            it[CLEAN_LECTURE_CACHE_AUTOMATICALLY] ?: CLEAN_LECTURE_CACHE_AUTOMATICALLY_DEFAULT
        }

    override suspend fun setCleanLectureCacheAutomatically(value: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[CLEAN_LECTURE_CACHE_AUTOMATICALLY] = value
        }
    }

    override val keepLecturesForDays: Flow<Int>
        get() = settingsDataStore.data.map {
            it[KEEP_LECTURES_FOR_DAYS] ?: KEEP_LECTURES_FOR_DAYS_DEFAULT
        }
    override suspend fun setKeepLecturesForDays(value: Int) {
        if (value < 0) {
            return
        }
        settingsDataStore.edit { preferences ->
            preferences[KEEP_LECTURES_FOR_DAYS] = value
        }
    }

    override suspend fun resetAll() {
        try {
            settingsDataStore.edit { preferences ->
                preferences[CACHE_MAIN_SCHEDULE] = CACHE_MAIN_SCHEDULE_DEFAULT
                preferences[CACHE_FAVORITE_SCHEDULES] = CACHE_FAVORITE_SCHEDULES_DEFAULT
                preferences[CACHE_IN_BACKGROUND] = CACHE_IN_BACKGROUND_DEFAULT
                preferences[BACKGROUND_CACHING_INTERVAL] = BACKGROUND_CACHING_INTERVAL_DEFAULT
                preferences[CLEAN_LECTURE_CACHE_AUTOMATICALLY] = CLEAN_LECTURE_CACHE_AUTOMATICALLY_DEFAULT
                preferences[KEEP_LECTURES_FOR_DAYS] = KEEP_LECTURES_FOR_DAYS_DEFAULT
            }
        }
        catch (_: Throwable){}
    }

    companion object {
        private val CACHE_MAIN_SCHEDULE = booleanPreferencesKey("cache_main")
        private val CACHE_FAVORITE_SCHEDULES = booleanPreferencesKey("cache_favorites")
        private val CACHE_IN_BACKGROUND = booleanPreferencesKey("cache_background")
        private val BACKGROUND_CACHING_INTERVAL = longPreferencesKey("cache_interval")
        private val CLEAN_LECTURE_CACHE_AUTOMATICALLY = booleanPreferencesKey("cache_auto_clean")
        private val KEEP_LECTURES_FOR_DAYS = intPreferencesKey("keep_cache_days")

        private const val CACHE_MAIN_SCHEDULE_DEFAULT : Boolean = true
        private const val CACHE_FAVORITE_SCHEDULES_DEFAULT : Boolean = true
        private const val CACHE_IN_BACKGROUND_DEFAULT : Boolean = false
        private const val BACKGROUND_CACHING_INTERVAL_DEFAULT : Long = 12 * 60 * 60
        private const val CLEAN_LECTURE_CACHE_AUTOMATICALLY_DEFAULT : Boolean = true
        private const val KEEP_LECTURES_FOR_DAYS_DEFAULT : Int = 7
    }
}