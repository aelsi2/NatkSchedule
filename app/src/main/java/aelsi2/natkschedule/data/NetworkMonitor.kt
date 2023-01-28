package aelsi2.natkschedule.data

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isOnline : Flow<Boolean>
}