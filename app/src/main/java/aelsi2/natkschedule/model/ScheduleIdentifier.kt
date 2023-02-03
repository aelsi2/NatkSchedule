package aelsi2.natkschedule.model

enum class ScheduleType(val numericValue : Int) {
    GROUP(0), TEACHER(1), CLASSROOM(2);
    companion object {
        fun fromInt(numericValue : Int?) = ScheduleType.values().firstOrNull { it.numericValue == numericValue }
    }
}

data class ScheduleIdentifier(
    val type: ScheduleType,
    val stringId : String
    )