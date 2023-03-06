package aelsi2.natkschedule.data.network

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isOnline : Flow<Boolean>
}