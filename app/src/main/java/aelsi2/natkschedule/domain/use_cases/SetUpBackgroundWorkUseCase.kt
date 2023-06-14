package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.SettingsReader
import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration

class SetUpBackgroundWorkUseCase(
    private val context: Context,
    private val settingsReader: SettingsReader
) {
    operator fun invoke() {
        MainScope().launch {
            combine(
                settingsReader.backgroundSyncEnabled,
                settingsReader.backgroundSyncIntervalHours
            ) { enabled, interval -> Pair(enabled, interval) }
                .collect {(enabled, interval) ->
                    if (enabled) {
                        enableOrUpdateWork(Duration.ofHours(interval.toLong()))
                    }
                    else {
                        disableWork()
                    }
                }
        }
    }

    private fun enableOrUpdateWork(cacheInterval: Duration) {
        val workRequest = PeriodicWorkRequestBuilder<BackgroundWorker>(
            repeatInterval = cacheInterval
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresDeviceIdle(true)
                .build()
        ).build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }
    private fun disableWork() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    companion object {
        private const val WORK_NAME = "natk_schedule_background_work"
    }
}

class BackgroundWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val doBackgroundWork: DoBackgroundWorkUseCase by inject()

    override suspend fun doWork(): Result {
        doBackgroundWork()
        return Result.success()
    }
}