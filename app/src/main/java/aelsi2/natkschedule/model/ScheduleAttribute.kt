package aelsi2.natkschedule.model

interface ScheduleAttribute {
    val scheduleIdentifier : ScheduleIdentifier
    val displayName: String
    fun matchesString(string: String): Boolean
}