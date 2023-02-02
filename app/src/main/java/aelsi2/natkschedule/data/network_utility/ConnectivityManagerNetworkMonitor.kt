package aelsi2.natkschedule.data.network_utility

import android.net.NetworkRequest.Builder
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build.VERSION
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

class ConnectivityManagerNetworkMonitor constructor(
    private val context: Context
) : NetworkMonitor {
    override val isOnline : Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                channel.trySend(connectivityManager.isConnected())
            }
            override fun onLost(network: Network) {
                channel.trySend(connectivityManager.isConnected())
            }
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                channel.trySend(connectivityManager.isConnected())
            }
        }
        connectivityManager?.registerNetworkCallback(
            Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build(),
            callback,
        )
        channel.trySend(connectivityManager.isConnected())
        awaitClose {
            connectivityManager?.unregisterNetworkCallback(callback)
        }
    }.conflate()
    private fun ConnectivityManager?.isConnected() : Boolean {
        this ?: return false
        @Suppress("DEPRECATION")
        return when {
            VERSION.SDK_INT > 29 -> activeNetwork?.let(::getNetworkCapabilities)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
            else -> activeNetworkInfo?.isConnected ?: false
        }
    }
}