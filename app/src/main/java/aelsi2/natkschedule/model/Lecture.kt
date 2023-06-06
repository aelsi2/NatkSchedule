package aelsi2.natkschedule.model

import java.time.LocalTime

data class Lecture(
    val index: Int?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val breakStartTime: LocalTime? = null,
    val breakEndTime: LocalTime? = null,
    val discipline: Discipline,
    val data: List<LectureData>
)