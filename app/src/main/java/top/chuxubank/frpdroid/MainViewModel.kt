package top.chuxubank.frpdroid

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import top.chuxubank.frpdroid.ConnectionState.*

class MainViewModel : ViewModel() {
    val connectionState = MutableLiveData(Disconnected)

    fun onToggleConnection() {
        connectionState.value = when (connectionState.value) {
            Connected -> Disconnected
            Connecting -> Disconnected
            Disconnected -> Connecting
            null -> Disconnected
        }
    }
}

enum class ConnectionState {
    Connected,
    Connecting,
    Disconnected
}