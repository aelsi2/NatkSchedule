package aelsi2.natkschedule.model

data class Group(
    val name : String,
    val programName : String?,
    val year : Int,
    val id : String = "${name}_${programName}_${year}"
) : LectureAttribute {
    override val scheduleIdentifier: ScheduleIdentifier
        get() = ScheduleIdentifier(ScheduleType.GROUP, id)
}