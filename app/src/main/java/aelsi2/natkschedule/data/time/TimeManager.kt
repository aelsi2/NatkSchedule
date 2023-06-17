package aelsi2.natkschedule.data.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TimeManager {
    val currentCollegeZonedDateTime: ZonedDateTime
        get() = ZonedDateTime.now().withZoneSameInstant(timeZone)
    val currentCollegeLocalDateTime: LocalDateTime
        get() = currentCollegeZonedDateTime.toLocalDateTime()
    val currentCollegeLocalDate: LocalDate
        get() = currentCollegeLocalDateTime.toLocalDate()
    val currentCollegeLocalTime: LocalTime
        get() = currentCollegeLocalDateTime.toLocalTime()

    val collegeZonedDateTime: Flow<ZonedDateTime>
        get() = flow {
            runEverySecond(runImmediately = true) {
                emit(currentCollegeZonedDateTime)
            }
        }.distinctUntilChanged()
    val collegeLocalDateTime: Flow<LocalDateTime>
        get() = flow {
            runEverySecond(runImmediately = true) {
                emit(currentCollegeLocalDateTime)
            }
        }.distinctUntilChanged()
    val collegeLocalDate: Flow<LocalDate>
        get() = flow {
            runEvery(10_000, runImmediately = true) {
                emit(currentCollegeLocalDate)
            }
        }.distinctUntilChanged()
    val collegeLocalTime: Flow<LocalTime>
        get() = flow {
            runEverySecond(runImmediately = true) {
                emit(currentCollegeLocalTime)
            }
        }.distinctUntilChanged()

    fun localToCollegeZoned(localDateTime: LocalDateTime): ZonedDateTime =
        localDateTime.atZone(timeZone)

    suspend fun runEverySecond(runImmediately: Boolean = true, action: suspend () -> Unit) =
        runEvery(ms = 1000, runImmediately = runImmediately, action = action)

    suspend fun runEvery(ms: Long, runImmediately: Boolean = true, action: suspend () -> Unit) {
        if (runImmediately) {
            action()
        }
        while (true) {
            val epochMs = System.currentTimeMillis()
            delay(ms - epochMs % ms)
            action()
        }
    }

    companion object {
        private const val TIME_ZONE_NAME = "Asia/Novosibirsk"
        private val timeZone = ZoneId.of(TIME_ZONE_NAME)
    }
}