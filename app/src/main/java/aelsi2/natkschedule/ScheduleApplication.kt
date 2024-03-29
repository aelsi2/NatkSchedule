package aelsi2.natkschedule

import aelsi2.natkschedule.data.background_work.backgroundWorkModule
import aelsi2.natkschedule.data.natk_database.natkDatabaseModule
import aelsi2.natkschedule.data.room_database.roomDatabaseModule
import aelsi2.natkschedule.data.network.networkModule
import aelsi2.natkschedule.data.preferences.preferencesModule
import aelsi2.natkschedule.data.repositories.demo.demoReposModule
import aelsi2.natkschedule.data.repositories.natk_database.natkDatabaseReposModule
import aelsi2.natkschedule.data.repositories.room_database.roomDatabaseReposModule
import aelsi2.natkschedule.data.time.timeModule
import aelsi2.natkschedule.domain.domainModule
import aelsi2.natkschedule.domain.use_cases.DoStartupWorkUseCase
import aelsi2.natkschedule.domain.use_cases.SetUpBackgroundSyncUseCase
import aelsi2.natkschedule.ui.viewModelsModule
import android.app.Application
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ScheduleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val koin = startKoin {
            androidContext(this@ScheduleApplication)
            modules(
                roomDatabaseModule,
                natkDatabaseModule,
                natkDatabaseReposModule,
                roomDatabaseReposModule,
                //demoReposModule,
                preferencesModule,
                networkModule,
                backgroundWorkModule,
                timeModule
            )
            modules(domainModule)
            modules(viewModelsModule)
        }.koin

        MainScope().launch {
            val doStartupWork: DoStartupWorkUseCase = koin.get()
            val setupBackgroundSync: SetUpBackgroundSyncUseCase = koin.get()
            doStartupWork()
            setupBackgroundSync()
        }
    }
}