package aelsi2.natkschedule.model

interface ScheduleAttribute {
    val scheduleIdentifier : ScheduleIdentifier
    fun matchesString(string: String): Boolean
}