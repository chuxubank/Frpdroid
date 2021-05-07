package top.chuxubank.frpdroid

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import top.chuxubank.frpdroid.ConnectionState.*
import top.chuxubank.frpdroid.FrpWorker.Companion.configDirName
import top.chuxubank.frpdroid.FrpWorker.Companion.configFileName
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        val TAG: String = MainViewModel::class.java.simpleName
        val FRPC_WORK = "FrpcWork"
    }

    val app = getApplication<Application>()
    val configFile = File(app.getExternalFilesDir(configDirName), configFileName)
    val connectionState = MutableLiveData(Disconnected)
    val addr = MutableLiveData("192.168.2.105")
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
        when (connectionState.value) {
            Connected, Connecting, null -> {
                connectionState.value = Disconnected
                WorkManager.getInstance(app).cancelUniqueWork(FRPC_WORK)
            }
            Disconnected -> {
                connectionState.value = Connecting
                configFile.writeText(config)
                val frpcWork = OneTimeWorkRequestBuilder<FrpWorker>().build()
                WorkManager.getInstance(app)
                    .beginUniqueWork(FRPC_WORK, ExistingWorkPolicy.KEEP, frpcWork).enqueue()
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