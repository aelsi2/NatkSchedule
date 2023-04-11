package aelsi2.natkschedule.model

data class Teacher(
    val fullName: String,
    val shortName: String? = null,
    val id: String = fullName
) : ScheduleAttribute {
    override val scheduleIdentifier: ScheduleIdentifier
        get() = ScheduleIdentifier(ScheduleType.TEACHER, id)

    override fun matchesString(string: String): Boolean =
        string.isEmpty() || fullName.contains(
            other = string,
            ignoreCase = true
        ) || shortName?.contains(
            other = string,
            ignoreCase = true
        ) ?: true
}

