package top.chuxubank.frpdroid

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import frpclib.Frpclib
import java.io.File

class FrpWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    companion object {
        const val configDirName = "config"
        const val configFileName = "frpc.ini"
    }

    override fun doWork(): Result {
        val configFile = File(applicationContext.getExternalFilesDir(configDirName), configFileName)
        return try {
            Runtime.getRuntime()
                .exec("su && setprop service.adb.tcp.port 5555 && stop adbd && start adbd")
            Frpclib.run(configFile.path)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}