package aelsi2.natkschedule.data.network_utility

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkUtilityModule = module {
    factoryOf(::ConnectivityManagerNetworkMonitor) bind NetworkMonitor::class
}