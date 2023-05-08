package aelsi2.natkschedule.data.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

class ConnectivityManagerNetworkMonitor constructor(
    private val connectivityManager: ConnectivityManager
) : NetworkMonitor {

    override val isOnline: StateFlow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                channel.trySend(network.hasInternet)
            }

            override fun onLost(network: Network) {
                channel.trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                channel.trySend(networkCapabilities.hasInternet)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        channel.trySend(connectivityManager.hasInternet)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.stateIn(MainScope(), SharingStarted.WhileSubscribed(5000), true)

    private val ConnectivityManager?.hasInternet : Boolean
        get() = this?.activeNetwork?.hasInternet ?: false

    private val Network?.hasInternet: Boolean
        get() = this?.let(connectivityManager::getNetworkCapabilities)?.hasInternet ?: false

    private val NetworkCapabilities?.hasInternet: Boolean
        get() = this?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
}