package aelsi2.natkschedule.data.network_utility

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isOnline : Flow<Boolean>
}