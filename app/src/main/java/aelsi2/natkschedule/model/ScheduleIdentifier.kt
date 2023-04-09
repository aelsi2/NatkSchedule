package aelsi2.natkschedule.model

import java.text.ParseException

enum class ScheduleType(val numericValue : Int) {
    GROUP(0), TEACHER(1), CLASSROOM(2);
    companion object {
        fun fromInt(numericValue : Int?) = ScheduleType.values().firstOrNull { it.numericValue == numericValue }
        fun fromString(string: String?) = ScheduleType.values().firstOrNull { it.name == string }
    }
}

data class ScheduleIdentifier(
    val type: ScheduleType,
    val stringId : String
) {
    override fun toString() = "${type}:$stringId"
    companion object {
        fun fromString(string: String?): ScheduleIdentifier? {
            string ?: return null
            val separatorIndex = string.indexOf(':')
            if (separatorIndex < 1) {
                return null
            }
            val scheduleType = ScheduleType.fromString(string.slice(0 until separatorIndex)) ?: return null
            val scheduleStringId = string.slice((separatorIndex + 1)..string.lastIndex)
            return ScheduleIdentifier(scheduleType, scheduleStringId)
        }
    }
}