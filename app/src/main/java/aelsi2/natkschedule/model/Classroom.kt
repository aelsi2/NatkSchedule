package aelsi2.natkschedule.model

data class Classroom(
    val fullName : String,
    val shortName : String? = null,
    val address : String? = null,
    val id : String = fullName
    ) : ScheduleAttribute {
    override val scheduleIdentifier: ScheduleIdentifier
        get() = ScheduleIdentifier(ScheduleType.CLASSROOM, id)
}