package aelsi2.natkschedule.domain.use_cases

import aelsi2.natkschedule.data.preferences.SettingsReader
import kotlinx.coroutines.flow.first

class DoStartupWorkUseCase(
    private val cleanUpCache: CleanCacheUseCase,
    private val settingsReader: SettingsReader
) {
    suspend operator fun invoke() {
        if (settingsReader.cleanOldSchedulesEnabled.first()) {
            cleanUpCache()
        }
    }
}