package aelsi2.natkschedule.model

data class Classroom(
    val fullName : String,
    val shortName : String? = null,
    val address : String? = null,
    override val id : String = fullName
    ) : LectureAttribute {
    override fun toScheduleIdentifier() = ScheduleIdentifier(ScheduleType.CLASSROOM, id)
}