package com.michaelliu.twsestockinfo.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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
            override fun onAvailable(network: Network) {
                // 不立即發送Available，等待onCapabilitiesChanged確認
                // 這樣可以避免"假可用"狀態
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                // 這裡可能會需要針對中國用戶做優化
                // 因為中國網路可能無法訪問國際伺服器 有可能會導致驗證不了
                val connected = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                )
                trySend(
                    if (connected) NetworkStatus.Available
                    else NetworkStatus.Unavailable
                )
            }

            override fun onLost(network: Network) {
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

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
        .distinctUntilChanged()

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
    data object UnKnown : NetworkStatus()
    data object Available : NetworkStatus()
    data object Unavailable : NetworkStatus()
}