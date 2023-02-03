package aelsi2.natkschedule

import aelsi2.natkschedule.data.database.databaseModule
import aelsi2.natkschedule.data.network_utility.networkUtilityModule
import aelsi2.natkschedule.data.preferences.preferencesModule
import aelsi2.natkschedule.data.repositories.repositoriesModule
import aelsi2.natkschedule.ui.viewModelsModule
import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ScheduleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(databaseModule, networkUtilityModule, preferencesModule, repositoriesModule)
            modules(viewModelsModule)
            androidContext(this@ScheduleApplication)
        }
    }
}