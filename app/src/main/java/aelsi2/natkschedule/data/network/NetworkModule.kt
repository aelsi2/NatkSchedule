package aelsi2.natkschedule.data.network

import android.content.Context
import android.net.ConnectivityManager
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::ConnectivityManagerNetworkMonitor) bind NetworkMonitor::class
    single { get<Context>().getSystemService(ConnectivityManager::class.java) }
}