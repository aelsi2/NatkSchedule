package aelsi2.natkschedule.model

import java.time.Instant

data class Lecture(
    val disciplineName : String,
    val startTime : Instant,
    val endTime : Instant,
    val teacher: Teacher?,
    val classroom: Classroom?,
    val group: Group?,
    val subgroupNumber : Int? = null,
    val id : String =
        "${startTime}_${teacher?.fullName}_${classroom?.fullName}_${group?.name}_${subgroupNumber}"
    ) {
    val ongoing : Boolean
        get() = (startTime < Instant.now()) and (endTime > Instant.now())
}
