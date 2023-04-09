package aelsi2.natkschedule.model

import java.time.LocalDate

data class ScheduleDay(
    val date: LocalDate,
    val lectures : List<Lecture>,
)
