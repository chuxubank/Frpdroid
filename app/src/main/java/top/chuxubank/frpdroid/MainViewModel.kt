package top.chuxubank.frpdroid

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import frpclib.Frpclib
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import top.chuxubank.frpdroid.ConnectionState.*
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        val TAG: String = MainViewModel::class.java.simpleName
    }

    val app = getApplication<Application>()
    val configDir = app.getExternalFilesDir("config")
    val configFile = File(configDir, "frpc.ini")
    val connectionState = MutableLiveData(Disconnected)
    val addr = MutableLiveData("192.168.2.129")
    val port = MutableLiveData("7000")
    val config =
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

    fun onToggleConnection() {
        connectionState.value = when (connectionState.value) {
            Connected -> Disconnected
            Connecting -> Disconnected
            Disconnected -> Connecting
            null -> Disconnected
        }
        configFile.writeText(config)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Frpclib.run(configFile.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
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