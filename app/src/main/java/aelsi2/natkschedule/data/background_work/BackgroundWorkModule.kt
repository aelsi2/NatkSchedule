package aelsi2.natkschedule.data.background_work

import androidx.work.WorkManager
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val backgroundWorkModule = module {
    single { WorkManager.getInstance(get()) }
}