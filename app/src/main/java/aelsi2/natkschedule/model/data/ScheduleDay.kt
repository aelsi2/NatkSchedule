package aelsi2.natkschedule.model.data

import java.time.LocalDate

data class ScheduleDay(
    val date : LocalDate,
    val scheduleItems : List<ScheduleItem>
    )
