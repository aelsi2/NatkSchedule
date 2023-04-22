package aelsi2.natkschedule.model

enum class ScheduleType(val numericValue : Int) {
    Group(0), Teacher(1), Classroom(2);
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