package aelsi2.natkschedule.data.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {
    val isOnline : StateFlow<Boolean>
}