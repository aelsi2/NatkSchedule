package aelsi2.natkschedule.data.time

import kotlinx.coroutines.delay
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
    fun localToCollegeZoned(localDateTime: LocalDateTime): ZonedDateTime = localDateTime.atZone(timeZone)
    suspend fun runEverySecond(runImmediately: Boolean = true, action: suspend () -> Unit) {
        if (runImmediately) {
            action()
        }
        while (true) {
            val epochMs = System.currentTimeMillis()
            delay(1000 - epochMs % 1000)
            action()
        }
    }
    companion object {
        private const val TIME_ZONE_NAME = "Asia/Novosibirsk"
        private val timeZone = ZoneId.of(TIME_ZONE_NAME)
    }
}