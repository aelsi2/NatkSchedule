package aelsi2.natkschedule.model

import java.util.StringJoiner

data class Teacher(
    val lastName: String,
    val firstName: String,
    val middleName: String? = null,
    override val id: String = "${firstName}_${middleName}_${lastName}"
) : LectureAttribute {
    val fullName : String
        get() {
            val joiner = StringJoiner(" ")
            if (lastName.isNotEmpty()) {
                joiner.add(lastName)
            }
            if (firstName.isNotEmpty()) {
                joiner.add(firstName)
            }
            if (!middleName.isNullOrEmpty()) {
                joiner.add(middleName)
            }
            return joiner.toString()
        }
    val shortName: String
        get() {
            val joiner = StringJoiner(" ")
            if (lastName.isNotEmpty()) {
                joiner.add(lastName)
            }
            if (firstName.isNotEmpty()) {
                joiner.add("${firstName.first()}.")
            }
            if (!middleName.isNullOrEmpty()) {
                joiner.add("${middleName.first()}.")
            }
            return joiner.toString()
        }

    override fun toScheduleIdentifier() = ScheduleIdentifier(ScheduleType.TEACHER, id)
}

