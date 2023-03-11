package aelsi2.natkschedule.model

import java.time.LocalDate
import java.time.LocalTime

data class Lecture(
    val disciplineName: String,
    val date: LocalDate,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val teacher: Teacher?,
    val classroom: Classroom?,
    val group: Group?,
    val subgroupNumber: Int? = null,
    val breakStartTime: LocalTime? = null,
    val breakEndTime: LocalTime? = null,
    val id: String =
        "${date}_${startTime}_${teacher?.fullName}_${classroom?.fullName}_${group?.name}_${subgroupNumber}"
)