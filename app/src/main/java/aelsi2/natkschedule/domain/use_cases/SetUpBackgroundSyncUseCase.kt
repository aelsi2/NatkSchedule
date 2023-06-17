package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.SettingsReader
import aelsi2.natkschedule.data.time.TimeManager
import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.await
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration

class SetUpBackgroundSyncUseCase(
    private val workManager: WorkManager,
    private val settingsReader: SettingsReader
) {
    operator fun invoke() {
        MainScope().launch {
            combine(
                settingsReader.backgroundSyncEnabled,
                settingsReader.backgroundSyncIntervalHours,
                settingsReader.saveMainScheduleEnabled,
                settingsReader.saveFavoritesEnabled
            ) { enabled, interval, saveMain, saveFavorites -> Pair(enabled && (saveMain || saveFavorites), interval) }
                .collect { (enabled, interval) ->
                    if (enabled) {
                        updateOrRelaunchWork(Duration.ofHours(interval.toLong()))
                    } else {
                        disableWork()
                    }
                }
        }
    }

    private suspend fun updateOrRelaunchWork(interval: Duration) {
        val intervalChanged = !workIntervalEquals(interval)
        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = interval
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).setInitialDelay(interval).addTag(makeIntervalTag(interval)).build()
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            if (intervalChanged)
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
            else ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun disableWork() {
        workManager.cancelUniqueWork(WORK_NAME)
    }

    private suspend fun workIntervalEquals(interval: Duration): Boolean {
        val info = workManager.getWorkInfosForUniqueWork(WORK_NAME).await().firstOrNull()
        if (info != null) {
            for (tag in info.tags) {
                if (tag == makeIntervalTag(interval)) {
                    return true
                }
            }
        }
        return false
    }

    private fun makeIntervalTag(duration: Duration) = "interval:${duration.toSeconds()}"

    companion object {
        private const val WORK_NAME = "natk_schedule_background_work"
    }
}

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val doBackgroundWork: DoBackgroundSyncUseCase by inject()

    override suspend fun doWork(): Result {
        println("Background worker running")
        doBackgroundWork()
        return Result.success()
    }
}