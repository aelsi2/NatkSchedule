package aelsi2.natkschedule.model

import java.time.LocalDateTime

data class ScheduleItem(
    val discipline : String,
    val startTime : LocalDateTime,
    val endTime : LocalDateTime,
    val teacher: Teacher,
    val classroom: Classroom,
    val group: Group,
    val subgroup : Int? = null,
    val id : String = "${startTime}_${group.name}_${subgroup}"
    ) {
    val ongoing : Boolean
        get() = (startTime < LocalDateTime.now()) and (LocalDateTime.now() < endTime)
}
