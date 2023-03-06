package aelsi2.natkschedule.model

data class Teacher(
    val fullName: String,
    val shortName: String? = null,
    val id: String = fullName
) : LectureAttribute {
    override val scheduleIdentifier: ScheduleIdentifier
        get() = ScheduleIdentifier(ScheduleType.TEACHER, id)
}

