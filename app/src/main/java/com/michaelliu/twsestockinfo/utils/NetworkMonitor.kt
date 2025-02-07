package com.michaelliu.twsestockinfo.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkStatus: Flow<NetworkStatus> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                trySend(NetworkStatus.Available)
            }
            override fun onLost(network: android.net.Network) {
                trySend(NetworkStatus.Unavailable)
            }
            override fun onUnavailable() {
                trySend(NetworkStatus.Unavailable)
            }
        }

        val networkResult = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkResult, networkCallback)

        trySend(getCurrentNetworkStatus())

        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }

    fun getCurrentNetworkStatus(): NetworkStatus {
        val network = connectivityManager.activeNetwork ?: return NetworkStatus.Unavailable
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkStatus.Unavailable

        return if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            NetworkStatus.Available
        } else {
            NetworkStatus.Unavailable
        }
    }

}

sealed class NetworkStatus {
    data object Available : NetworkStatus()
    data object Unavailable : NetworkStatus()
}