package aelsi2.natkschedule.data.network

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    factoryOf(::ConnectivityManagerNetworkMonitor) bind NetworkMonitor::class
}