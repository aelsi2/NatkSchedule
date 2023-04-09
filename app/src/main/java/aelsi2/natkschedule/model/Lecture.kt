package aelsi2.natkschedule.model

import java.time.LocalDate
import java.time.LocalTime

data class Lecture(
    val index: Int?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val breakStartTime: LocalTime? = null,
    val breakEndTime: LocalTime? = null,
    val data: List<LectureData>
)