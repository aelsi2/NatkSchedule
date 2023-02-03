package aelsi2.natkschedule.model

interface LectureAttribute {
    val id : String
    fun toScheduleIdentifier() : ScheduleIdentifier
}