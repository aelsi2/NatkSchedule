package aelsi2.natkschedule.data.preferences

import aelsi2.natkschedule.data.preferences.datastore.DataStoreSettingsManager
import aelsi2.natkschedule.data.preferences.datastore.DataStoreFavoritesManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

val preferencesModule = module {
    singleOf(::DataStoreSettingsManager) binds arrayOf(SettingsReader::class, SettingsManager::class)
    singleOf(::DataStoreFavoritesManager) binds arrayOf(FavoritesReader::class, FavoritesManager::class)
}