package aelsi2.natkschedule.model

import java.time.Instant

data class Lecture(
    val discipline : String,
    val startTime : Instant,
    val endTime : Instant,
    val teacher: Teacher?,
    val classroom: Classroom?,
    val group: Group?,
    val subgroup : Int? = null,
    val id : String =
        "${startTime}_${teacher?.getFullName()}_${classroom?.fullName}_${group?.name}_${subgroup}"
    ) {
    val ongoing : Boolean
        get() = (startTime < Instant.now()) and (endTime > Instant.now())
}
