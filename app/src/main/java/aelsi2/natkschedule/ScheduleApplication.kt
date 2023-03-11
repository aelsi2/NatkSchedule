package aelsi2.natkschedule

import aelsi2.natkschedule.data.room_database.roomDatabaseModule
import aelsi2.natkschedule.data.network.networkModule
import aelsi2.natkschedule.data.preferences.preferencesModule
import aelsi2.natkschedule.data.repositories.natk_database.natkDatabaseReposModule
import aelsi2.natkschedule.data.repositories.room_database.roomDatabaseReposModule
import aelsi2.natkschedule.data.time.timeModule
import aelsi2.natkschedule.domain.domainModule
import aelsi2.natkschedule.ui.viewModelsModule
import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ScheduleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ScheduleApplication)
            modules(
                roomDatabaseModule,
                networkModule,
                preferencesModule,
                natkDatabaseReposModule,
                roomDatabaseReposModule,
                timeModule
            )
            modules(domainModule)
            modules(viewModelsModule)
        }
    }
}