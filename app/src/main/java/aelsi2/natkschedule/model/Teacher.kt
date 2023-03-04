package aelsi2.natkschedule.model

import java.util.StringJoiner

data class Teacher(
    val fullName: String,
    val shortName: String? = null,
    override val id: String = fullName
) : LectureAttribute {
    override fun toScheduleIdentifier() = ScheduleIdentifier(ScheduleType.TEACHER, id)
}

