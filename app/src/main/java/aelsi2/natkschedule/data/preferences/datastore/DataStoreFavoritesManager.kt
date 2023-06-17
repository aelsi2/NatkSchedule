package aelsi2.natkschedule.data.preferences.datastore

import aelsi2.natkschedule.data.preferences.FavoritesManager
import aelsi2.natkschedule.model.ScheduleIdentifier
import aelsi2.natkschedule.model.ScheduleType
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import org.json.JSONException
import org.json.JSONObject

private const val DATASTORE_NAME: String = "favorites"

private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

class DataStoreFavoritesManager(appContext: Context) : FavoritesManager {
    private val favoritesDataStore = appContext.dataStore
    private val favoritesDataStoreData = favoritesDataStore.data.shareIn(
        MainScope(), SharingStarted.Eagerly, replay = 1
    )

    override val mainScheduleId: Flow<ScheduleIdentifier?> = favoritesDataStoreData.map {
        it[MAIN_SCHEDULE].toScheduleIdentifier() ?: MAIN_SCHEDULE_DEFAULT
    }

    override suspend fun setMainScheduleId(value: ScheduleIdentifier?) {
        favoritesDataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(MAIN_SCHEDULE)
            } else {
                preferences[MAIN_SCHEDULE] = value.toJsonString()
            }
        }
    }

    override val favoriteScheduleIds: Flow<List<ScheduleIdentifier>> =
        favoritesDataStoreData.map { preferences ->
            preferences[FAVORITE_SCHEDULES]?.mapNotNull {
                it.toScheduleIdentifier()
            } ?: listOf()
        }

    override fun isInFavorites(scheduleId: ScheduleIdentifier): Flow<Boolean> =
        favoriteScheduleIds.map {
            scheduleId in it
        }

    override suspend fun addToFavorites(schedule: ScheduleIdentifier) {
        val favorites = favoriteScheduleIds.first().toMutableSet()
        favorites.add(schedule)
        setFavorites(favorites)
    }

    override suspend fun removeFromFavorites(schedule: ScheduleIdentifier) {
        val favorites = favoriteScheduleIds.first().toMutableSet()
        favorites.remove(schedule)
        setFavorites(favorites)
    }

    override suspend fun clearFavorites() {
        favoritesDataStore.edit {
            it.remove(FAVORITE_SCHEDULES)
        }
    }

    private suspend fun setFavorites(favorites: Set<ScheduleIdentifier>) {
        favoritesDataStore.edit { preferences ->
            preferences[FAVORITE_SCHEDULES] = favorites.map { it.toJsonString() }.toSet()
        }
    }

    companion object {
        private val MAIN_SCHEDULE = stringPreferencesKey("main")
        private val FAVORITE_SCHEDULES = stringSetPreferencesKey("favorites")

        private val MAIN_SCHEDULE_DEFAULT = null
    }
}

private const val JSON_SCHEDULE_TYPE_KEY = "type"
private const val JSON_SCHEDULE_ID_KEY = "id"

private fun ScheduleIdentifier.toJsonString(): String {
    val json = JSONObject()
    json.put(JSON_SCHEDULE_TYPE_KEY, type.numericValue)
    json.put(JSON_SCHEDULE_ID_KEY, stringId)
    return json.toString()
}

private fun String?.toScheduleIdentifier(): ScheduleIdentifier? {
    try {
        val json = JSONObject(this ?: return null)
        val type = ScheduleType.fromInt(json.getInt(JSON_SCHEDULE_TYPE_KEY)) ?: return null
        val stringId = json.getString(JSON_SCHEDULE_ID_KEY)
        return ScheduleIdentifier(type, stringId)
    } catch (e: JSONException) {
        return null
    }
}