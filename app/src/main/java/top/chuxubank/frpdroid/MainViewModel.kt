package top.chuxubank.frpdroid

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import frpclib.Frpclib
import kotlinx.coroutines.launch
import top.chuxubank.frpdroid.ConnectionState.*

class MainViewModel : ViewModel() {
    val connectionState = MutableLiveData(Disconnected)
    val addr = MutableLiveData("")
    val port = MutableLiveData("")

    fun onToggleConnection() {
        connectionState.value = when (connectionState.value) {
            Connected -> Disconnected
            Connecting -> Disconnected
            Disconnected -> Connecting
            null -> Disconnected
        }
        viewModelScope.launch {
            Frpclib.run(
                """
                    [common]
                    server_addr = ${addr.value}
                    server_port = ${port.value}
                    
                    [ssh]
                    type = tcp
                    local_ip = 127.0.0.1
                    local_port = 5555
                    remote_port = 1234
                    """.trimIndent()
            )
        }
    }

    fun onAddressChange(address: String) {
        addr.value = address
    }

    fun onPortChange(port: String) {
        port.toIntOrNull()?.takeIf { it < 65535 }?.let {
            this.port.value = it.toString()
        }
        if (port.isBlank()) this.port.value = ""
    }
}

enum class ConnectionState {
    Connected,
    Connecting,
    Disconnected
}